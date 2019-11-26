package cc.popkorn.compiler

import cc.popkorn.PROVIDER_MAPPINGS
import cc.popkorn.PROVIDER_SUFFIX
import cc.popkorn.RESOLVER_MAPPINGS
import cc.popkorn.RESOLVER_SUFFIX
import cc.popkorn.annotations.Exclude
import cc.popkorn.annotations.ForEnvironments
import cc.popkorn.annotations.Injectable
import cc.popkorn.annotations.InjectableProvider
import cc.popkorn.compiler.generators.ExternalProviderGenerator
import cc.popkorn.compiler.generators.InternalProviderGenerator
import cc.popkorn.compiler.generators.MappingGenerator
import cc.popkorn.compiler.generators.ResolverGenerator
import cc.popkorn.compiler.models.DefaultImplementation
import cc.popkorn.compiler.utils.*
import cc.popkorn.core.Provider
import com.sun.tools.javac.code.Type.ClassType
import java.io.File
import java.util.*
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.MirroredTypesException
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.ElementFilter
import javax.tools.StandardLocation


/**
 * AbstractProcessor to process all PopKorn annotations and generates the necessary source code files
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class PopKornCompiler : AbstractProcessor() {
    private lateinit var directory : File
    private lateinit var logger : Logger

    companion object {
        const val KAPT_KOTLIN_GENERATED = "kapt.kotlin.generated"
    }

    override fun getSupportedOptions(): MutableSet<String> {
        return mutableSetOf(KAPT_KOTLIN_GENERATED)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String?> {
        return mutableSetOf(Injectable::class.qualifiedName, InjectableProvider::class.qualifiedName, Exclude::class.qualifiedName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        directory = processingEnv
            .options[KAPT_KOTLIN_GENERATED]
            ?.let { File(it) }
            ?.apply { mkdir() }
            ?: throw PopKornException("Can't find the target directory for generated Kotlin files.")
        logger = Logger(processingEnv.messager)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (roundEnv.processingOver()) return false

        val internalProviderClasses = roundEnv.getInjectableClasses()
        val externalProviderClasses = roundEnv.getProviderClasses()
        val excludedInterfaces = roundEnv.getExcludedInterfaces()
        val aliasMapper = getAliasMapper(internalProviderClasses, externalProviderClasses)

        val internalProviderGenerator = InternalProviderGenerator(directory, processingEnv.typeUtils, aliasMapper)
        internalProviderClasses.forEach { internalProviderGenerator.write(it) }

        val externalProviderGenerator = ExternalProviderGenerator(directory)
        externalProviderClasses.forEach { externalProviderGenerator.write(it.key, it.value) }

        val resolverGenerator = ResolverGenerator(directory)
        val interfaces = getInterfaces(internalProviderClasses, externalProviderClasses, excludedInterfaces)
        interfaces.forEach { (i, c) -> resolverGenerator.write(i, c) }

        //TODO
        val moduleName = internalProviderClasses.first().getModuleName().replace("-", "")

        val mappingGenerator = MappingGenerator(directory)
        val mapResolver = "cc.popkorn.mapping.$moduleName${RESOLVER_SUFFIX}Mapping".also { mappingGenerator.writeResolvers(it, interfaces.keys) }
        val mapProvider = "cc.popkorn.mapping.$moduleName${PROVIDER_SUFFIX}Mapping".also { mappingGenerator.writeProviders(it, internalProviderClasses + externalProviderClasses.keys.toList()) }

        processingEnv.filer.writeMappings(RESOLVER_MAPPINGS, mapResolver)
        processingEnv.filer.writeMappings(PROVIDER_MAPPINGS, mapProvider)

        return true
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

    private fun RoundEnvironment.getProviderClasses() : Map<TypeElement, TypeElement>{
        val injectableElements = ElementFilter.typesIn(getAll(InjectableProvider::class))
            .groupBy { element ->
                element.takeUnless { it.isInterface() } ?: throw PopKornException("InjectableProvider cannot be interfaces: $element")

                //TODO version 1.2 should accept injectable parameters to the providers
                element.getConstructors().singleOrNull()?.takeIf { it.parameters.size==0 } ?: throw PopKornException(
                    "InjectableProvider can't have parametrized constructor: $element"
                )
                val inter = element.interfaces.find { processingEnv.typeUtils.erasure(it).isSame(Provider::class) } ?: throw PopKornException(
                    "InjectableProvider must implement Provider<*>: $element"
                )
                (inter as? ClassType)?.allparams_field?.singleOrNull()?.asElement() as? TypeElement ?: throw PopKornException(
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


    private fun RoundEnvironment.getExcludedInterfaces() : List<TypeMirror>{
        return getAll(Exclude::class)
            .filter { it.kind == ElementKind.INTERFACE }
            .map { it.asType() }
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


    private fun getInterfaces(int: List<TypeElement>, ext:Map<TypeElement, TypeElement>, exclusions:List<TypeMirror>) : Map<TypeMirror, List<DefaultImplementation>>{
        val map = hashMapOf<TypeMirror, ArrayList<DefaultImplementation>>()
        int.forEach { map.addInjectableInterfaces(it, it, exclusions) }
        ext.forEach { map.addInjectableInterfaces(it.key, it.value, exclusions) }
        return map
    }

    private fun HashMap<TypeMirror, ArrayList<DefaultImplementation>>.addInjectableInterfaces(element: TypeElement, container:TypeElement, exclusions:List<TypeMirror>) {
        val environments = ArrayList<String?>()
        container.get(ForEnvironments::class)?.value?.takeIf { it.isNotEmpty() }?.also { environments.addAll(it) } ?: environments.add(null)
        val hereExclusions = element.get(Injectable::class)?.getExclusions() ?: arrayListOf()

        val envElem = DefaultImplementation(element, environments)
        element.getHierarchyElements(exclusions + hereExclusions).forEach { this.getOrPut(it) { arrayListOf() }.add(envElem) }
    }


    private fun TypeElement.getHierarchyElements(exclusions:List<TypeMirror>) : List<TypeMirror>{
        return this.interfaces
            .filterNot { type ->
                exclusions.any { processingEnv.typeUtils.isSameType(type, it) }
            }

        //All interfaces including those from superclass
//        val s = this.superclass?.let { processingEnv.typeUtils.asElement(it) as? TypeElement }?.let { it.getHierarchyElements() } ?: arrayListOf()
//        val i = this.interfaces
//        return s + i
    }


    private fun Injectable.getExclusions() : List<TypeMirror>{
        return try {
            this.exclude.map { processingEnv.elementUtils.getTypeElement(it.qualifiedName).asType() }
        }catch(e: MirroredTypesException){
            return e.typeMirrors
        }catch(e: MirroredTypeException){
            return listOf(e.typeMirror)
        }
    }


    private fun Filer.writeMappings(resource:String, content:String){
        createResource(StandardLocation.CLASS_OUTPUT, "", resource)
            .openWriter()
            .also { it.write(content) }
            .close()
    }

}