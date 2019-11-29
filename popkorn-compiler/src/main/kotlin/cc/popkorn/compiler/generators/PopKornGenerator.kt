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
import cc.popkorn.compiler.utils.Logger
import cc.popkorn.compiler.utils.getAll
import cc.popkorn.core.Provider
import com.sun.tools.javac.code.Type
import java.io.File
import java.util.ArrayList
import java.util.HashMap
import javax.annotation.processing.Filer
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.MirroredTypesException
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.StandardLocation

/**
 * Class to process all PopKorn annotations and generates the necessary source code files
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class PopKornGenerator(generatedSourcesDir:File, private val filer: Filer, private val types: Types, private val elements: Elements, private val logger:Logger) {
    private val internalProviderGenerator = InternalProviderGenerator(generatedSourcesDir, types)
    private val externalProviderGenerator = ExternalProviderGenerator(generatedSourcesDir)
    private val resolverGenerator = ResolverGenerator(generatedSourcesDir)
    private val mappingGenerator = MappingGenerator(generatedSourcesDir, filer)

    private val resolverMappings = hashMapOf<TypeElement,String>()
    private val providerMappings = hashMapOf<TypeElement,String>()


    fun process(roundEnv: RoundEnvironment) {
        val internalProviderClasses = roundEnv.getInjectableClasses()
        val externalProviderClasses = roundEnv.getInjectableProviderClasses()
        val interfacesClasses = getInterfaces(internalProviderClasses, externalProviderClasses)
        val aliasMapper = getAliasMapper(internalProviderClasses, externalProviderClasses)

        logger.message("Writing Injectable Classes: ${internalProviderClasses.size + externalProviderClasses.size}")
        internalProviderClasses.forEach { providerMappings[it] = internalProviderGenerator.write(it, aliasMapper) }
        externalProviderClasses.forEach { providerMappings[it.key] = externalProviderGenerator.write(it.key, it.value) }
        interfacesClasses.forEach { (i, c) -> resolverMappings[i] = resolverGenerator.write(i, c) }
        logger.message("Done writing Injectable Classes")

        //If its the last round, write mappings
        if (roundEnv.processingOver()){
            logger.message("Writing Mappings")
            mappingGenerator.write(resolverMappings, RESOLVER_SUFFIX, RESOLVER_MAPPINGS)
            mappingGenerator.write(providerMappings, PROVIDER_SUFFIX, PROVIDER_MAPPINGS)
            logger.message("Done writing Mappings")

            filer.createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/proguard/popkorn.pro")
                .openWriter()
                .also { it.write("-keep class * implements cc.popkorn.mapping.Mapping") }
                .close()

        }

    }



    private fun RoundEnvironment.getInjectableClasses() : List<TypeElement>{
        val injectableElements = getElementsAnnotatedWith(Injectable::class.java)
        injectableElements.forEach { element ->
            if (element.kind == ElementKind.INTERFACE) throw PopKornException("@Injectable is not applicable to interfaces: $element")
            element.enclosingElement?.takeIf { it.kind != ElementKind.PACKAGE }?.let { throw PopKornException("@Injectable is not applicable to inner classes: $element") }
            if (element.modifiers.contains(Modifier.ABSTRACT)) throw PopKornException("@Injectable is not applicable to abstract classes: $element")
            if (!element.modifiers.contains(Modifier.PUBLIC) && !element.modifiers.contains(Modifier.PROTECTED)) throw PopKornException("@Injectable is not applicable to private classes: $element")
        }

        return injectableElements.map { it as TypeElement }
    }

    private fun RoundEnvironment.getInjectableProviderClasses() : Map<TypeElement, TypeElement>{
        val injectableElements = ElementFilter.typesIn(getAll(InjectableProvider::class))
            .groupBy { element ->
                element.takeUnless { it.isInterface() } ?: throw PopKornException("InjectableProvider cannot be interfaces: $element")

                //TODO version 1.2.0 should accept injectable parameters to the providers
                element.getConstructors().singleOrNull()?.takeIf { it.parameters.size==0 } ?: throw PopKornException(
                    "InjectableProvider can't have parametrized constructor: $element"
                )
                val inter = element.interfaces.find { types.erasure(it).isSame(Provider::class) } ?: throw PopKornException(
                    "InjectableProvider must implement Provider<*>: $element"
                )
                (inter as? Type.ClassType)?.allparams_field?.singleOrNull()?.asElement() as? TypeElement ?: throw PopKornException(
                    "InjectableProvider is malformed, please review it: $element"
                )
            }
            .mapValues { it.value.singleOrNull() ?: throw PopKornException("${it.value.joinToString()} are providing the same class: ${it.key}") }


        injectableElements.forEach { entry ->
            entry.key.takeUnless { it.has(Injectable::class) } ?: throw PopKornException("InjectableProvider ${entry.value} not needed, as ${entry.key} has already been annotated with @Injectable")
            entry.key.takeUnless { it.isInterface() } ?: throw PopKornException("InjectableProviders cannot provide interfaces: ${entry.value} is providing ${entry.key}")
        }

        return injectableElements
    }



    private fun getAliasMapper(int: List<TypeElement>, ext:Map<TypeElement, TypeElement>) : Map<String, TypeMirror>{
        val map = hashMapOf<String, ArrayList<TypeElement>>()

        int.forEach { element ->
            element.get(Injectable::class)
                ?.alias?.takeIf { it.isNotEmpty() }
                ?.also { map.getOrPut(it) { arrayListOf() }.add(element) }
        }

        ext.forEach { (element, provider) ->
            provider.get(InjectableProvider::class)
                ?.alias?.takeIf { it.isNotEmpty() }
                ?.also { map.getOrPut(it) { arrayListOf() }.add(element) }
        }

        return map.mapValues {
            it.value.singleOrNull()?.asType() ?: throw PopKornException("${it.value.joinToString()} has the same alias: ${it.key}")
        }

    }


    private fun getInterfaces(int: List<TypeElement>, ext:Map<TypeElement, TypeElement>) : Map<TypeElement, List<DefaultImplementation>>{
        val map = hashMapOf<TypeElement, ArrayList<DefaultImplementation>>()
        int.forEach { map.addInjectableInterfaces(it, it) }
        ext.forEach { map.addInjectableInterfaces(it.key, it.value) }
        return map
    }

    private fun HashMap<TypeElement, ArrayList<DefaultImplementation>>.addInjectableInterfaces(element: TypeElement, container:TypeElement) {
        val environments = ArrayList<String?>()
        container.get(ForEnvironments::class)?.value?.takeIf { it.isNotEmpty() }?.also { environments.addAll(it) } ?: environments.add(null)
        val exclusions = element.get(Injectable::class)?.getExclusions() ?: arrayListOf()

        val envElem = DefaultImplementation(element, environments)
        element.getHierarchyElements(exclusions).forEach { this.getOrPut(it) { arrayListOf() }.add(envElem) }
    }


    private fun TypeElement.getHierarchyElements(exclusions:List<TypeMirror>) : List<TypeElement>{
        return this.getAllInterfaces()
            .toSet()
            .filterNot { type -> exclusions.any { types.isSameType(type, it) } }
            .mapNotNull { types.asElement(it) as? TypeElement }
            .filterNot { it.has(Exclude::class) }
    }

    private fun TypeElement.getAllInterfaces() : List<TypeMirror>{
        val s = this.superclass?.let { types.asElement(it) as? TypeElement }?.getAllInterfaces() ?: arrayListOf()
        val i = this.interfaces
        val iMore = this.interfaces.mapNotNull { types.asElement(it) as? TypeElement }.map { it.getAllInterfaces() }.flatten()
        return s + i + iMore
    }



    private fun Injectable.getExclusions() : List<TypeMirror>{
        return try {
            this.exclude.map { elements.getTypeElement(it.qualifiedName).asType() }
        }catch(e: MirroredTypesException){
            return e.typeMirrors
        }catch(e: MirroredTypeException){
            return listOf(e.typeMirror)
        }
    }


}