package cc.popkorn.compiler.generators

import cc.popkorn.PROVIDER_MAPPINGS
import cc.popkorn.PROVIDER_SUFFIX
import cc.popkorn.RESOLVER_MAPPINGS
import cc.popkorn.RESOLVER_SUFFIX
import cc.popkorn.annotations.Exclude
import cc.popkorn.annotations.ForEnvironments
import cc.popkorn.annotations.Injectable
import cc.popkorn.annotations.InjectableProvider
import cc.popkorn.compiler.PopKornException
import cc.popkorn.compiler.models.DefaultImplementation
import cc.popkorn.compiler.utils.*
import cc.popkorn.core.Propagation
import java.io.File
import java.util.*
import javax.annotation.processing.Filer
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.MirroredTypesException
import javax.lang.model.type.PrimitiveType
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.StandardLocation

/**
 * Class to handle all PopKorn annotations and generates the necessary source code files
 *
 * @author Pau Corbella
 * @since 1.1.0
 */
internal class MainGenerator(generatedSourcesDir: File, private val filer: Filer, private val types: Types, private val elements: Elements, private val logger: Logger) {
    private val providerGenerator = ProviderGenerator(generatedSourcesDir, types)
    private val resolverGenerator = ResolverGenerator(generatedSourcesDir)
    private val mappingGenerator = MappingGenerator(generatedSourcesDir, filer)

    private val resolverMappings = hashMapOf<TypeElement, String>()
    private val providerMappings = hashMapOf<TypeElement, String>()


    // At the beginning we don't need to do anything
    fun init() {
        logger.message("PopKorn Start Compiling")
    }

    // For every round, we write all providers and resolvers for the elements received
    fun process(roundEnv: RoundEnvironment) {
        val directInjectableClasses = roundEnv.getDirectInjectableClasses()
        val providedInjectableClasses = roundEnv.getProvidedInjectableClasses()
        val interfacesClasses = getInterfaces(directInjectableClasses, providedInjectableClasses)
        val aliasMapper = getAliasMapper(directInjectableClasses, providedInjectableClasses)

        logger.message("Generating providers of ${directInjectableClasses.size} direct injectable classes")
        directInjectableClasses.forEach { providerMappings[it] = providerGenerator.write(it, aliasMapper) }
        logger.message("Generating providers of ${providedInjectableClasses.size} provided injectable classes")
        providedInjectableClasses.forEach { providerMappings[it.key] = providerGenerator.write(it.key, it.value, aliasMapper) }
        logger.message("Generating ${interfacesClasses.size} resolvers")
        interfacesClasses.forEach { (i, c) -> resolverMappings[i] = resolverGenerator.write(i, c) }
    }

    // At the end, we save the mappings and proguard
    fun end() {
        logger.message("Generating Mappings")
        mappingGenerator.write(resolverMappings, RESOLVER_SUFFIX, RESOLVER_MAPPINGS)
        mappingGenerator.write(providerMappings, PROVIDER_SUFFIX, PROVIDER_MAPPINGS)

        filer.createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/proguard/popkorn.pro")
            .openWriter()
            .also { it.write("-keep class * implements cc.popkorn.mapping.Mapping") }
            .close()
    }


    private fun RoundEnvironment.getDirectInjectableClasses(): List<TypeElement> {
        val injectableElements = ElementFilter.typesIn(getElementsAnnotatedWith(Injectable::class.java))
        injectableElements.forEach { it.checkConstruction() }
        return injectableElements.toList()
    }

    private fun RoundEnvironment.getProvidedInjectableClasses(): Map<TypeElement, TypeElement> {
        return ElementFilter.typesIn(getElementsAnnotatedWith(InjectableProvider::class.java))
            .groupBy { element ->
                element.checkConstruction()
                resolveType(element).also { it.checkVisibility() }
            }
            .mapValues { it.value.singleOrNull() ?: throw PopKornException("Only one InjectableProvider per type is allowed: ${it.value.joinToString()} are providing the same class: ${it.key}") }
    }


    // Gets the injectable element from an InjectableProvider
    private fun resolveType(element: TypeElement): TypeElement {
        return element.getMethods()
            .also { if (it.isEmpty()) throw PopKornException("$element must contain at least one public method") }
            .mapNotNull {
                if (it.returnType is PrimitiveType) {
                    types.boxedClass(it.returnType as PrimitiveType)
                } else {
                    types.asElement(it.returnType) as? TypeElement
                }
            }
            .distinct()
            .singleOrNull()
            ?.also { if (it.has(Injectable::class)) throw PopKornException("$element not needed, as $it has already been annotated with @Injectable") }
            ?: throw PopKornException("All public methods in $element must return the same type. Remember that in Kotlin, getters and setters are generated automatically for properties)")
    }


    // Gets a mapping of alias-elements defined by Injectable and InjectableProvider annotations
    private fun getAliasMapper(direct: List<TypeElement>, provided: Map<TypeElement, TypeElement>): Map<String, TypeMirror> {
        val map = hashMapOf<String, ArrayList<TypeElement>>()

        direct.forEach { element ->
            element.get(Injectable::class)
                ?.alias?.takeIf { it.isNotEmpty() }
                ?.also { map.getOrPut(it) { arrayListOf() }.add(element) }
        }

        provided.forEach { (element, provider) ->
            provider.get(InjectableProvider::class)
                ?.alias?.takeIf { it.isNotEmpty() }
                ?.also { map.getOrPut(it) { arrayListOf() }.add(element) }
        }

        return map.mapValues {
            it.value.singleOrNull()?.asType() ?: throw PopKornException("${it.value.joinToString()} has the same alias: ${it.key}")
        }

    }


    // Gets a list of all supertypes (of Injectable and InjectableProvider) that will also be injectable
    private fun getInterfaces(direct: List<TypeElement>, provided: Map<TypeElement, TypeElement>): Map<TypeElement, List<DefaultImplementation>> {
        val map = hashMapOf<TypeElement, ArrayList<DefaultImplementation>>()
        direct.forEach {
            val environments = it.get(ForEnvironments::class).toList()
            val exclusions = it.get(Injectable::class).getExclusions()
            val propagation = it.get(Injectable::class)?.propagation ?: Propagation.ALL
            val envElem = DefaultImplementation(it, environments)
            it.getHierarchyElements(propagation, exclusions).forEach { inter -> map.getOrPut(inter) { arrayListOf() }.add(envElem) }
        }
        provided.forEach {
            val environments = it.value.get(ForEnvironments::class).toList()
            val exclusions = it.value.get(InjectableProvider::class).getExclusions()
            val propagation = it.value.get(InjectableProvider::class)?.propagation ?: Propagation.NONE
            val envElem = DefaultImplementation(it.key, environments)
            it.key.getHierarchyElements(propagation, exclusions).forEach { inter -> map.getOrPut(inter) { arrayListOf() }.add(envElem) }
        }
        return map
    }


    // Checks that a TypeElement can be constructed
    private fun TypeElement.checkConstruction() {
        checkVisibility()
        if (isInterface()) throw PopKornException("$this can not be an interface. Only classes are allowed")
        if (isAbstract()) throw PopKornException("$this can not be an abstract class")
        if (getConstructors().isEmpty()) throw PopKornException("Could not find any public constructor of $this")
    }

    // Checks that a TypeElement is visible to be used
    private fun TypeElement.checkVisibility() {
        if (isInner()) throw PopKornException("$this can not be an inner class")
        if (isPrivate()) throw PopKornException("$this cannot be a private class")
    }


    // Gets a list of all interfaces of an Element (taking into account the Propagation and Exclusion)
    private fun TypeElement.getHierarchyElements(propagation: Propagation, exclusions: List<TypeMirror>): List<TypeElement> {
        val parents = when (propagation) {
            Propagation.NONE -> arrayListOf()
            Propagation.DIRECT -> interfaces + this.superclass?.takeIf { it.isAbstract() }
            Propagation.ALL -> getAllInterfaces()
        }

        return parents
            .asSequence()
            .filterNotNull()
            .toSet()
            .filterNot { type -> exclusions.any { types.isSameType(type, it) } }
            .mapNotNull { types.asElement(it) as? TypeElement }
            .filterNot { it.has(Exclude::class) }
            .let {
                if (this.isInterface() || this.isAbstract()) it + this
                else it
            }
    }

    // Gets a list of all interfaces of an element
    private fun TypeElement.getAllInterfaces(): List<TypeMirror?> {
        val parent = this.superclass?.takeIf { it.isAbstract() }
        val s = this.superclass?.let { types.asElement(it) as? TypeElement }?.getAllInterfaces() ?: arrayListOf()
        val i = this.interfaces
        val iMore = this.interfaces.mapNotNull { types.asElement(it) as? TypeElement }.map { it.getAllInterfaces() }.flatten()
        return s + i + iMore + parent
    }


    // Gets the exclusions of the element annotated with Injectable
    // Because the excluded class is not yet compiled, it will throw an exception
    // We can catch it and get the corresponding element
    private fun Injectable?.getExclusions(): List<TypeMirror> {
        return try {
            this?.exclude?.map { elements.getTypeElement(it.qualifiedName).asType() } ?: arrayListOf()
        } catch (e: MirroredTypesException) {
            return e.typeMirrors
        } catch (e: MirroredTypeException) {
            return listOf(e.typeMirror)
        }
    }

    // Gets the exclusions of the element annotated with InjectableProvider
    // Because the excluded class is not yet compiled, it will throw an exception
    // We can catch it and get the corresponding element
    private fun InjectableProvider?.getExclusions(): List<TypeMirror> {
        return try {
            this?.exclude?.map { elements.getTypeElement(it.qualifiedName).asType() } ?: arrayListOf()
        } catch (e: MirroredTypesException) {
            return e.typeMirrors
        } catch (e: MirroredTypeException) {
            return listOf(e.typeMirror)
        }
    }

    // Gets a list of all environments defined in the annotation ForEnvironments
    private fun ForEnvironments?.toList(): List<String?> {
        val environments = ArrayList<String?>()
        this?.value?.takeIf { it.isNotEmpty() }?.also { environments.addAll(it) } ?: environments.add(null)
        return environments
    }


    // Checks if a TypeMirror is an abstract class
    private fun TypeMirror.isAbstract(): Boolean {
        val element = types.asElement(this) as? TypeElement
        return element?.isAbstract() ?: false
    }


}