package cc.popkorn.compiler.generators

import cc.popkorn.core.Provider
import cc.popkorn.Scope
import cc.popkorn.compiler.utils.PROVIDER_SUFFIX
import cc.popkorn.compiler.utils.splitPackage
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.lang.model.element.*
import javax.lang.model.type.TypeMirror

/**
 * Class to generate Provider files based on @InjectableProvider annotation
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class ExternalProviderGenerator(private val directory: File) {

    fun write(clazz: TypeElement, provider: TypeElement) {
        val property = PropertySpec.builder("inner", provider.asType().asTypeName())
            .delegate("lazy { ${provider.simpleName}() }")
            .build()

        val file = getFile(clazz.asType(), property)
        file.writeTo(directory)

    }



    private fun getFile(element:TypeMirror, property:PropertySpec) : FileSpec {

        val createFun = FunSpec.builder("create")
            .addParameter("environment", String::class.asTypeName().copy(nullable = true))
            .addModifiers(KModifier.OVERRIDE)
            .returns(element.asTypeName())
            .addCode("return ${property.name}.create(environment)\n")
            .build()

        val scopeFun = FunSpec.builder("scope")
            .addModifiers(KModifier.OVERRIDE)
            .returns(Scope::class)
            .addCode("return ${property.name}.scope()\n")
            .build()

        val pack = "${element}_$PROVIDER_SUFFIX".splitPackage()
        return FileSpec.builder(pack.first, pack.second)
            .addImport("cc.popkorn", "inject")
            .addType(
                TypeSpec.classBuilder(pack.second)
                    .addSuperinterface(Provider::class.asClassName().parameterizedBy(element.asTypeName()))
                    .addProperty(property)
                    .addFunction(createFun)
                    .addFunction(scopeFun)
                    .build()
            )
            .build()
    }


}