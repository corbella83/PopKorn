package cc.popkorn.compiler.generators

import cc.popkorn.PROVIDER_SUFFIX
import cc.popkorn.Scope
import cc.popkorn.annotations.*
import cc.popkorn.compiler.PopKornException
import cc.popkorn.compiler.utils.*
import cc.popkorn.compiler.utils.get
import cc.popkorn.compiler.utils.getConstructors
import cc.popkorn.compiler.utils.has
import cc.popkorn.compiler.utils.splitPackage
import cc.popkorn.core.Provider
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Types

/**
 * Class to generate Provider files based on @Injectable annotation
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class InternalProviderGenerator(private val directory: File, private val typeUtils: Types) {

    fun write(clazz: TypeElement, namesMapper: Map<String, TypeMirror>) : String {
        val creationCode = getCreationCode(clazz, namesMapper)

        val filePackage = "${clazz.qualifiedName}_$PROVIDER_SUFFIX"
        val scope = clazz.get(Injectable::class)?.scope ?: Scope.BY_APP
        val file = getFile(filePackage, clazz.asClassName(), creationCode, scope, clazz.isInternal())
        file.writeTo(directory)
        return filePackage
    }

    private fun getCreationCode(clazz: TypeElement, namesMapper: Map<String, TypeMirror>) : CodeBlock {
        val environments = clazz.getAvailableEnvironments()

        val codeBlock = CodeBlock.builder()
        if (environments.isEmpty()) { //If no environments are defined, return the default constructor
            codeBlock.add("return ${create(clazz, namesMapper)}")
        } else {
            codeBlock.add("return when(environment){\n")
            environments.forEach { env ->
                codeBlock.add("    \"$env\" -> ${create(clazz, namesMapper, env)}\n")
            }
            codeBlock.add("    else -> ${create(clazz, namesMapper)}\n")
            codeBlock.add("}\n")
        }

        return codeBlock.build()
    }


    private fun create(element: TypeElement, namesMapper: Map<String, TypeMirror>, environment: String? = null): String {
        val constructor = if (environment == null) element.getDefaultConstructor() else element.getConstructor(environment)

        val params = constructor.parameters.map { param ->
            val nextEnv = param.get(WithEnvironment::class)?.value
            val impl = param.get(Alias::class)?.value
                ?.let {
                    val alternate = namesMapper[it] ?: throw PopKornException("Could not find any Injectable class with name $it")
                    if (!typeUtils.isAssignable(alternate, param.asType())) throw PopKornException(
                        "Parameter specified as $it (${alternate}) is not assignable to ${param.asType()} while creating $element"
                    )
                    alternate
                }
                ?: param.asType().asTypeName().toString()

            if (nextEnv != null) {
                "inject<$impl>(\"$nextEnv\")"
            } else {
                "inject<$impl>()"
            }
        }

        return "$element(${params.joinToString()})"
    }


    private fun getFile(filePackage:String, className:ClassName, creationCode:CodeBlock, scope: Scope, int:Boolean) : FileSpec {
        val createFun = FunSpec.builder("create")
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
            .addImport("cc.popkorn", "inject")
            .addType(
                TypeSpec.classBuilder(pack.second)
                    .apply { if (int) addModifiers(KModifier.INTERNAL) }
                    .addSuperinterface(Provider::class.asClassName().parameterizedBy(className))
                    .addFunction(createFun)
                    .addFunction(scopeFun)
                    .build()
            )
            .build()
    }


    //Returns the list of all environments defined in constructors with the annotation @ForEnvironments
    // If an environment is defined more than one time, throws an exception
    private fun TypeElement.getAvailableEnvironments(): List<String> {
        val list = getConstructors()
            .map { it.get(ForEnvironments::class)?.value ?: arrayOf() }.toTypedArray()
            .flatten()

        return list.takeIf { HashSet(list).size == it.size } ?: throw PopKornException("$this has more than one constructor for the same environment")
    }


    //Returns the default constructor of this TypeElement (the one that does not have the annotation @ForEnvironments
    private fun TypeElement.getDefaultConstructor() : ExecutableElement {
        val constructors = getConstructors()
            .filter { !it.has(ForEnvironments::class) }

        return constructors.singleOrNull() ?: throw PopKornException("$qualifiedName should have only one public constructor or use environments")
    }

    //Returns the constructor that matches the environment with the ones provided in annotation @ForEnvironments
    //If no constructor is found returns the default constructor
    private fun TypeElement.getConstructor(environment:String) : ExecutableElement {
        val constructors = getConstructors()
            .filter { it.get(ForEnvironments::class)?.value?.contains(environment) ?: false }

        return when (constructors.size){
            0 -> getDefaultConstructor()
            1 -> constructors.single()
            else -> throw PopKornException("$qualifiedName should have only one public constructor for environment $environment")
        }
    }


}