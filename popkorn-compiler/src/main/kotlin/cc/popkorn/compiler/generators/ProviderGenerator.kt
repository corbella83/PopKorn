package cc.popkorn.compiler.generators

import cc.popkorn.ALTERNATE_JAVA_LANG_PACKAGE
import cc.popkorn.Environment
import cc.popkorn.PROVIDER_SUFFIX
import cc.popkorn.Scope
import cc.popkorn.annotations.*
import cc.popkorn.compiler.PopKornException
import cc.popkorn.compiler.utils.*
import cc.popkorn.compiler.utils.get
import cc.popkorn.core.Injector
import cc.popkorn.core.Provider
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Types
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap
import kotlin.reflect.jvm.internal.impl.name.FqName

/**
 * Class to generate Provider files based on classes annotated with @Injectable and @InjectableProvider
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
internal class ProviderGenerator(private val directory: File, private val typeUtils: Types) {

    // Writes a provider from a direct injectable element
    fun write(element: TypeElement, namesMapper: Map<String, TypeMirror>) : String {
        val creationCode = getCreationCode(element.getConstructors(), namesMapper, "$element should have only one public constructor or use environments", "Found multiple constructors with the same environment in $element")

        val scope = element.get(Injectable::class)?.scope ?: Scope.BY_APP
        val file = element.getProviderFile(null, creationCode, scope)
        file.writeTo(directory)
        return "${file.packageName}.${file.name}"
    }


    // Writes a provider from a provided injectable element
    fun write(element: TypeElement, provider: TypeElement, namesMapper: Map<String, TypeMirror>) : String {
        val property = PropertySpec.builder("inner", provider.asClassName())
            .addModifiers(KModifier.PRIVATE)
            .delegate("lazy { ${provider.simpleName}() }")
            .build()

        val creationCode = getCreationCode(provider.getMethods(), namesMapper, "$provider should have only one public method or use environments", "Found multiple methods with the same environment in $provider")
        val scope = provider.get(InjectableProvider::class)?.scope ?: Scope.BY_APP
        val file = element.getProviderFile(property, creationCode, scope)
        file.writeTo(directory)
        return "${file.packageName}.${file.name}"
    }




    private fun getCreationCode(list: List<ExecutableElement>, namesMapper: Map<String, TypeMirror>, errorNone:String, errorDuplicated:String) : CodeBlock{
        val elements = list.map { it to (it.get(ForEnvironments::class)?.value ?: arrayOf()) }.toMap()

        val default = elements.filterValues { it.isEmpty() }.keys.singleOrNull()
            ?: throw PopKornException(errorNone)

        val others = elements.toMutableMap().apply { remove(default) }
            .apply {
                val all = values.map { it.toList() }.flatten()
                if (all.size != all.distinct().size) throw PopKornException(errorDuplicated)
            }

        val codeBlock = CodeBlock.builder()
        if (others.isEmpty()){
            codeBlock.add("return ${default.getCreationString(namesMapper)}")
        }else{
            codeBlock.add("return when(environment){\n")
            others.forEach { (exe, env) ->
                val environmentsList = env.joinToString { "\"$it\"" }
                codeBlock.add("    $environmentsList -> ${exe.getCreationString(namesMapper)}\n")
            }
            codeBlock.add("    else -> ${default.getCreationString(namesMapper)}\n")
            codeBlock.add("}\n")
        }

        return codeBlock.build()
    }


    private fun ExecutableElement.getCreationString(namesMapper: Map<String, TypeMirror>): String {
        val params = parameters.map { param ->
            if (param.asType().asTypeName() == Environment::class.asTypeName()){
                return@map "${param.asType().asTypeName()}(environment)"
            }

            val nextEnv = param.get(WithEnvironment::class)?.value
            val impl = param.get(Alias::class)?.value
                ?.let {
                    val alternate = namesMapper[it] ?: throw PopKornException("Could not find any Injectable class with name $it")
                    if (!typeUtils.isAssignable(alternate, param.asType())) throw PopKornException("Parameter specified as $it (${alternate}) is not assignable to ${param.asType()} while creating $this")
                    alternate
                }
                ?: param.asType().asTypeName().toString()

            if (nextEnv != null) {
                "injector.inject($impl::class, \"$nextEnv\")"
            } else {
                "injector.inject($impl::class)"
            }
        }

        return if (kind == ElementKind.CONSTRUCTOR){
            "$enclosingElement(${params.joinToString()})"
        }else{
            "inner.$simpleName(${params.joinToString()})"
        }

    }




    private fun TypeElement.getProviderFile(property: PropertySpec?, creationCode: CodeBlock, scope: Scope) : FileSpec {
        val filePackage = "${qualifiedName}_$PROVIDER_SUFFIX".replace(ALTERNATE_JAVA_LANG_PACKAGE.first, ALTERNATE_JAVA_LANG_PACKAGE.second)

        val className = supportClassName()

        val createFun = FunSpec.builder("create")
            .addParameter("injector", Injector::class)
            .addParameter("environment", String::class.asTypeName().copy(nullable = true))
            .addModifiers(KModifier.OVERRIDE)
            .returns(className)
            .addCode(creationCode)
            .build()

        val scopeFun = FunSpec.builder("scope")
            .addModifiers(KModifier.OVERRIDE)
            .returns(Scope::class)
            .addCode("return Scope.$scope")
            .build()

        val pack = filePackage.splitPackage()
        return FileSpec.builder(pack.first, pack.second)
            .addType(
                TypeSpec.classBuilder(pack.second)
                    .apply { if (isInternal()) addModifiers(KModifier.INTERNAL) }
                    .addSuperinterface(Provider::class.asClassName().parameterizedBy(className))
                    .apply { if (property!=null) addProperty(property) }
                    .addFunction(createFun)
                    .addFunction(scopeFun)
                    .build()
            )
            .build()
    }

    private fun TypeElement.supportClassName() : ClassName{
        return this.takeUnless { isKotlinClass() }
            ?.let { JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(it.toString())) }
            ?.asSingleFqName()
            ?.let { ClassName.bestGuess(it.asString()) }
            ?: asClassName()
    }


}