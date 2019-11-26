package cc.popkorn.compiler.generators

import cc.popkorn.compiler.PopKornException
import cc.popkorn.compiler.models.DefaultImplementation
import cc.popkorn.RESOLVER_SUFFIX
import cc.popkorn.compiler.utils.splitPackage
import cc.popkorn.core.Resolver
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass

/**
 * Class to generate Resolver files based on interfaces of @Injectable and @InjectableProvider classes
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class ResolverGenerator(private val directory: File) {

    fun write(inter:TypeMirror, classes:List<DefaultImplementation>) {
        val resolverCode = getResolverCode(classes)

        val file = getFile(inter, resolverCode)
        file.writeTo(directory)
    }

    private fun getResolverCode(classes:List<DefaultImplementation>) : CodeBlock {
        val environments = classes.getAvailableEnvironments()

        val codeBlock = CodeBlock.builder()
        if (environments.isEmpty()) { //If no environments are defined, return the default constructor
            codeBlock.add("return ${classes.getDefaultImplementation()}::class")
        } else {
            codeBlock.add("return when(environment){\n")
            environments.forEach { env ->
                codeBlock.add("    \"$env\" -> ${classes.getImplementation(env)}::class\n")
            }
            codeBlock.add("    else -> ${classes.getDefaultImplementation()}::class\n")
            codeBlock.add("}\n")
        }

        return codeBlock.build()
    }


    private fun getFile(element:TypeMirror, creationCode:CodeBlock) : FileSpec {
        val producerOf = WildcardTypeName.producerOf(element.asTypeName())

        val create = FunSpec.builder("resolve")
            .addParameter("environment", String::class.asTypeName().copy(nullable = true))
            .addModifiers(KModifier.OVERRIDE)
            .returns(KClass::class.asClassName().parameterizedBy(producerOf))
            .addCode(creationCode)
            .build()

        val pack = "${element}_$RESOLVER_SUFFIX".splitPackage()
        return FileSpec.builder(pack.first, pack.second)
            .addType(
                TypeSpec.classBuilder(pack.second)
                    .addSuperinterface(Resolver::class.asClassName().parameterizedBy(element.asTypeName()))
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

    private fun List<DefaultImplementation>.getDefaultImplementation() : TypeElement {
        val elements = this
            .filter { it.environments.contains(null) }

        if (elements.isEmpty()) throw PopKornException("Default Injectable not found: ${this.map { it.element }.joinToString()}")
        return elements.singleOrNull()?.element ?: throw PopKornException("More than one class is default Injectable: ${this.map { it.element }.joinToString()}")
    }


    private fun List<DefaultImplementation>.getImplementation(environment:String) : TypeElement {
        val elements = this
            .filter { it.environments.contains(environment) }

        return when (elements.size){
            0 -> getDefaultImplementation()
            1 -> elements.single().element
            else -> throw PopKornException("Environment must be unique among ${this.map { it.element }.joinToString()}")
        }
    }

}