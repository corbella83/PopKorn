package cc.popkorn.compiler.generators

import cc.popkorn.core.Provider
import cc.popkorn.Scope
import cc.popkorn.PROVIDER_SUFFIX
import cc.popkorn.compiler.utils.isInternal
import cc.popkorn.compiler.utils.splitPackage
import cc.popkorn.core.Injector
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File
import javax.lang.model.element.*

/**
 * Class to generate Provider files based on @InjectableProvider annotation
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class ExternalProviderGenerator(private val directory: File) {

    fun write(clazz: TypeElement, provider: TypeElement) : String {
        val property = PropertySpec.builder("inner", provider.asClassName())
            .addModifiers(KModifier.PRIVATE)
            .delegate("lazy { ${provider.simpleName}() }")
            .build()

        val filePackage = "${clazz.qualifiedName}_$PROVIDER_SUFFIX"
        val file = getFile(filePackage, clazz.asClassName(), property, clazz.isInternal())
        file.writeTo(directory)
        return filePackage
    }



    private fun getFile(filePackage:String, className:ClassName, property:PropertySpec, int:Boolean) : FileSpec {

        val createFun = FunSpec.builder("create")
            .addParameter("injector", Injector::class)
            .addParameter("environment", String::class.asTypeName().copy(nullable = true))
            .addModifiers(KModifier.OVERRIDE)
            .returns(className)
            .addCode("return ${property.name}.create(injector, environment)\n")
            .build()

        val scopeFun = FunSpec.builder("scope")
            .addModifiers(KModifier.OVERRIDE)
            .returns(Scope::class)
            .addCode("return ${property.name}.scope()\n")
            .build()

        val pack = filePackage.splitPackage()
        return FileSpec.builder(pack.first, pack.second)
            .addType(
                TypeSpec.classBuilder(pack.second)
                    .apply { if (int) addModifiers(KModifier.INTERNAL) }
                    .addSuperinterface(Provider::class.asClassName().parameterizedBy(className))
                    .addProperty(property)
                    .addFunction(createFun)
                    .addFunction(scopeFun)
                    .build()
            )
            .build()
    }


}