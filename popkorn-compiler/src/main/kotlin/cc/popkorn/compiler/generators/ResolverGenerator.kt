package cc.popkorn.compiler.generators

import cc.popkorn.compiler.PopKornException
import cc.popkorn.compiler.models.DefaultImplementation
import cc.popkorn.RESOLVER_SUFFIX
import cc.popkorn.compiler.utils.isInternal
import cc.popkorn.compiler.utils.splitPackage
import cc.popkorn.core.Resolver
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import kotlin.reflect.KClass

/**
 * Class to generate Resolver files based on interfaces of @Injectable and @InjectableProvider classes
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class ResolverGenerator(private val directory: File) {

    fun write(inter:TypeElement, classes:List<DefaultImplementation>) : String {
        val resolverCode = getResolverCode(inter, classes)

        val filePackage = "${getGenerationName(inter)}_$RESOLVER_SUFFIX"
        val file = getFile(filePackage, inter.asClassName(), resolverCode, inter.isInternal())
        file.writeTo(directory)
        return filePackage
    }

    private fun getResolverCode(inter:TypeElement, classes:List<DefaultImplementation>) : CodeBlock {
        val environments = classes.getAvailableEnvironments()

        val codeBlock = CodeBlock.builder()
        if (environments.isEmpty()) { //If no environments are defined, return the default constructor
            codeBlock.add("return ${classes.getDefaultImplementation(inter)}::class")
        } else {
            codeBlock.add("return when(environment){\n")
            environments.forEach { env ->
                codeBlock.add("    \"$env\" -> ${classes.getImplementation(inter, env)}::class\n")
            }
            codeBlock.add("    else -> ${classes.getDefaultImplementation(inter)}::class\n")
            codeBlock.add("}\n")
        }

        return codeBlock.build()
    }


    private fun getFile(filePackage:String, className:ClassName, creationCode:CodeBlock, int:Boolean) : FileSpec {
        val producerOf = WildcardTypeName.producerOf(className)

        val create = FunSpec.builder("resolve")
            .addParameter("environment", String::class.asTypeName().copy(nullable = true))
            .addModifiers(KModifier.OVERRIDE)
            .returns(KClass::class.asClassName().parameterizedBy(producerOf))
            .addCode(creationCode)
            .build()

        val pack = filePackage.splitPackage()
        return FileSpec.builder(pack.first, pack.second)
            .addType(
                TypeSpec.classBuilder(pack.second)
                    .apply { if (int) addModifiers(KModifier.INTERNAL) }
                    .addSuperinterface(Resolver::class.asClassName().parameterizedBy(className))
                    .addFunction(create)
                    .build()
            )
            .build()
    }



    private fun List<DefaultImplementation>.getAvailableEnvironments(): List<String> {
        val list = this
            .map { it.environments }
            .flatten()
            .filterNotNull()
            .sorted()

        return list.takeIf { HashSet(list).size == it.size } ?: throw PopKornException("Environment must be unique among ${this.map { it.element }.joinToString()}")
    }

    private fun List<DefaultImplementation>.getDefaultImplementation(inter:TypeElement) : TypeElement {
        val elements = this
            .filter { it.environments.contains(null) }

        if (elements.isEmpty()) throw PopKornException("Default Injectable not found for $inter: ${this.map { it.element }.joinToString()}")
        return elements.singleOrNull()?.element ?: throw PopKornException("$inter has more than one class default Injectable: ${this.map { it.element }.joinToString()}")
    }


    private fun List<DefaultImplementation>.getImplementation(inter:TypeElement, environment:String) : TypeElement {
        val elements = this
            .filter { it.environments.contains(environment) }

        return when (elements.size){
            0 -> getDefaultImplementation(inter)
            1 -> elements.single().element
            else -> throw PopKornException("$inter has more than one class for environment $environment: ${this.map { it.element }.joinToString()}")
        }
    }


    private fun getGenerationName(element:TypeElement) : String{
        val parent = element.enclosingElement?.takeIf { it.kind == ElementKind.INTERFACE || it.kind == ElementKind.CLASS }
        return if (parent==null){ //If the class its on its own
            element.toString()
        }else{
            "${parent}_${element.simpleName}"
        }
    }

}