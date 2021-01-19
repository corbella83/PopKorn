package cc.popkorn.compiler.generators

import cc.popkorn.InjectorManager
import cc.popkorn.PROVIDER_SUFFIX
import cc.popkorn.annotations.*
import cc.popkorn.compiler.PopKornException
import cc.popkorn.compiler.utils.*
import cc.popkorn.core.Injector
import cc.popkorn.core.Scope
import cc.popkorn.core.config.Parameters
import cc.popkorn.core.exceptions.DefaultConstructorNotFoundException
import cc.popkorn.core.exceptions.DefaultMethodNotFoundException
import cc.popkorn.core.model.Empty
import cc.popkorn.core.model.Environment
import cc.popkorn.normalizeQualifiedName
import cc.popkorn.providers.Provider
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.jetbrains.annotations.Nullable
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
    fun writeDirect(element: TypeElement, namesMapper: Map<String, TypeMirror>): String {
        val scope = element.get(Injectable::class)?.scope ?: Scope.BY_APP
        val creationCode = getCreationCode(element.getConstructors(), namesMapper, element.asClassName(), DefaultConstructorNotFoundException::class.asClassName(), scope)
        val file = element.getProviderFile(null, creationCode, scope)
        file.writeTo(directory)
        return "${file.packageName}.${file.name}"
    }


    // Writes a provider from a provided injectable element
    fun writeProvided(element: TypeElement, provider: TypeElement, namesMapper: Map<String, TypeMirror>): String {
        val property = PropertySpec.builder("inner", provider.asClassName())
            .addModifiers(KModifier.PRIVATE)
            .delegate("lazy { ${provider.simpleName}() }")
            .build()

        val scope = provider.get(InjectableProvider::class)?.scope ?: Scope.BY_APP
        val creationCode = getCreationCode(provider.getMethods(), namesMapper, provider.asClassName(), DefaultMethodNotFoundException::class.asClassName(), scope)
        val file = element.getProviderFile(property, creationCode, scope)
        file.writeTo(directory)
        return "${file.packageName}.${file.name}"
    }


    private fun getCreationCode(list: List<ExecutableElement>, namesMapper: Map<String, TypeMirror>, caller: ClassName, error: ClassName, scope: Scope): CodeBlock {
        val elements = list.map { it to (it.get(ForEnvironments::class)?.value ?: arrayOf()) }.toMap()

        val default = elements.filterValues { it.isEmpty() }.keys.let {
            if (it.size > 1) throw PopKornException("$caller has more than one default constructor/method with default environment")
            it.singleOrNull()
        }

        val others = elements.toMutableMap().apply { if (default != null) remove(default) }
            .apply {
                val all = values.map { it.toList() }.flatten()
                if (all.size != all.distinct().size) throw PopKornException("$caller has more than one constructor/method for the same environment")
            }

        val defaultCode = default?.getCreationString(namesMapper, scope) ?: "throw $error(\"$caller\")"

        val codeBlock = CodeBlock.builder()
        if (others.isEmpty()) {
            codeBlock.add("return $defaultCode")
        } else {
            codeBlock.add("return when(environment){\n")
            others.forEach { (exe, env) ->
                val environmentsList = env.joinToString { "\"$it\"" }
                codeBlock.add("    $environmentsList -> ${exe.getCreationString(namesMapper, scope)}\n")
            }
            codeBlock.addStatement("    else -> $defaultCode\n")
            codeBlock.add("}\n")
        }

        return codeBlock.build()
    }

    private fun ExecutableElement.getCreationString(namesMapper: Map<String, TypeMirror>, scope: Scope): String {
        val params = parameters.map { param ->
            if (param.asType().asTypeName() == Injector::class.asTypeName()) {
                throw PopKornException("Constructors cannot use 'Injector' as a parameter, use 'InjectorManager' instead")
            } else if (param.asType().asTypeName() == InjectorManager::class.asTypeName()) {
                return@map "injector"
            } else if (param.asType().asTypeName() == Environment::class.asTypeName()) {
                return@map "${param.asType().asTypeName()}(environment)"
            } else if (param.asType().asTypeName() == Empty::class.asTypeName()) {
                return@map "${param.asType().asTypeName()}()"
            }

            val nextEnv = param.get(WithEnvironment::class)?.value
            val impl = param.get(Alias::class)?.value
                ?.let {
                    val alternate = namesMapper[it] ?: throw PopKornException("Could not find any Injectable class with name $it")
                    if (!typeUtils.isAssignable(alternate, param.asType())) throw PopKornException("Parameter specified as $it ($alternate) is not assignable to ${param.asType()} while creating $this")
                    alternate
                }
                ?: param.asType()

            val name = impl.supportTypeName().toString()

            if (param.has(Assisted::class)) {
                if (scope != Scope.BY_NEW) throw PopKornException("Cannot use assisted dependencies with a scope other than BY_NEW: $this. Either change the scope or use an injectable parameter.")
                constructCall("assisted", if (param.has(Nullable::class)) "getOrNull" else "get", name, nextEnv)
            } else {
                constructCall("injector", if (param.has(Nullable::class)) "injectOrNull" else "inject", name, nextEnv)
            }
        }

        return if (kind == ElementKind.CONSTRUCTOR) {
            "$enclosingElement(${params.joinToString()})"
        } else {
            "inner.$simpleName(${params.joinToString()})"
        }

    }

    private fun constructCall(caller: String, method: String, clazz: String, env: String?): String {
        return if (env != null) {
            "$caller.$method($clazz::class, \"$env\")"
        } else {
            "$caller.$method($clazz::class)"
        }
    }

    private fun TypeElement.getProviderFile(property: PropertySpec?, creationCode: CodeBlock, scope: Scope): FileSpec {
        val filePackage = "${normalizeQualifiedName(qualifiedName.toString())}_$PROVIDER_SUFFIX"

        val className = supportClassName()

        val createFun = FunSpec.builder("create")
            .addParameter("injector", InjectorManager::class)
            .addParameter("assisted", Parameters::class)
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
                    .addKdoc("Automatically generated by PopKorn. DO NOT MODIFY\nhttps://github.com/corbella83/PopKorn")
                    .apply { if (isInternal()) addModifiers(KModifier.INTERNAL) }
                    .addSuperinterface(Provider::class.asClassName().parameterizedBy(className))
                    .apply { if (property != null) addProperty(property) }
                    .addFunction(createFun)
                    .addFunction(scopeFun)
                    .build()
            )
            .build()
    }

    private fun TypeElement.supportClassName(): ClassName {
        return this.takeUnless { isKotlinClass() }
            ?.let { toKotlin(it.toString()) }
            ?: asClassName()
    }

    private fun TypeMirror.supportTypeName(): TypeName {
        return this.takeUnless { it.getAnnotation(Metadata::class.java) != null }
            ?.let { toKotlin(it.toString()) }
            ?: asTypeName()
    }

    private fun toKotlin(name: String): ClassName? {
        return JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(FqName(name))
            ?.asSingleFqName()
            ?.let { ClassName.bestGuess(it.asString()) }
    }

}
