package cc.popkorn.compiler.generators

import cc.popkorn.compiler.utils.PROVIDER_SUFFIX
import cc.popkorn.compiler.utils.RESOLVER_SUFFIX
import mapping.Mapping
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

    fun writeResolvers(moduleName:String, list:Set<TypeMirror>) {
        val code = getCode(list, RESOLVER_SUFFIX)
        val file = getFile(code, "$moduleName$RESOLVER_SUFFIX")
        file.writeTo(directory)
    }


    fun writeProviders(moduleName:String, list:List<TypeElement>) {
        val code = getCode(list.map { it.asType() }.toSet(), PROVIDER_SUFFIX)
        val file = getFile(code, "$moduleName$PROVIDER_SUFFIX")
        file.writeTo(directory)
    }


    private fun getCode(list:Set<TypeMirror>, suffix:String):CodeBlock {
        val function = CodeBlock.builder()
        function.add("return when(original){\n")
        list.forEach {
            function.add("    $it::class -> ${it}_$suffix::class\n")
        }
        function.add("    else -> null\n")
        function.add("}\n")
        return function.build()
    }

    private fun getFile(creationCode:CodeBlock, filename:String) : FileSpec {
        val create = FunSpec.builder("find")
            .addParameter("original", KClass::class.asClassName().parameterizedBy(STAR))
            .addModifiers(KModifier.OVERRIDE)
            .returns(KClass::class.asClassName().parameterizedBy(STAR).copy(nullable = true))
            .addCode(creationCode)
            .build()

        val name = "${filename}Mapping"
        return FileSpec.builder("cc.popkorn.mapping", name)
            .addType(
                TypeSpec.classBuilder(name)
                    .addSuperinterface(Mapping::class)
                    .addFunction(create)
                    .build()
            )
            .build()
    }

}