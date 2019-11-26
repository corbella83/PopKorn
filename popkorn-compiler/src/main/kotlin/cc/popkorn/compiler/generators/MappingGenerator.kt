package cc.popkorn.compiler.generators

import cc.popkorn.PROVIDER_SUFFIX
import cc.popkorn.RESOLVER_SUFFIX
import cc.popkorn.compiler.utils.splitPackage
import cc.popkorn.mapping.Mapping
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass

/**
 * Class to generate Mappings for this module
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class MappingGenerator(private val directory: File) {

    fun writeResolvers(className:String, list:Set<TypeMirror>) {
        val code = getCode(list, RESOLVER_SUFFIX)
        val file = getFile(code, className)
        return file.writeTo(directory)
    }


    fun writeProviders(className:String, list:List<TypeElement>) {
        val code = getCode(list.map { it.asType() }.toSet(), PROVIDER_SUFFIX)
        val file = getFile(code, className)
        file.writeTo(directory)
    }


    private fun getCode(list:Set<TypeMirror>, suffix:String):CodeBlock {
        val function = CodeBlock.builder()
        function.add("return when(original){\n")
        list.forEach {
            function.add("    $it::class -> ${it}_$suffix()\n")
        }
        function.add("    else -> null\n")
        function.add("}\n")
        return function.build()
    }

    private fun getFile(creationCode:CodeBlock, qualifiedName:String) : FileSpec {
        val producerOfAny = WildcardTypeName.producerOf(Any::class)
        val create = FunSpec.builder("find")
            .addParameter("original", KClass::class.asClassName().parameterizedBy(producerOfAny))
            .addModifiers(KModifier.OVERRIDE)
            .returns(Any::class.asClassName().copy(nullable = true))
            .addCode(creationCode)
            .build()


        val pack = qualifiedName.splitPackage()
        return FileSpec.builder(pack.first, pack.second)
            .addType(
                TypeSpec.classBuilder(pack.second)
                    .addSuperinterface(Mapping::class)
                    .addFunction(create)
                    .build()
            )
            .build()
    }

}