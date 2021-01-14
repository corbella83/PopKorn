package cc.popkorn.compiler

import cc.popkorn.annotations.Injectable
import cc.popkorn.annotations.InjectableProvider
import cc.popkorn.compiler.generators.MainGenerator
import cc.popkorn.compiler.utils.Logger
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement

/**
 * AbstractProcessor to handle all PopKorn annotations and generate the necessary source code files
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
internal class PopKornCompiler : AbstractProcessor() {
    private lateinit var mainGenerator: MainGenerator

    companion object {
        const val KAPT_KOTLIN_GENERATED = "kapt.kotlin.generated"
    }

    override fun getSupportedOptions(): MutableSet<String> {
        return mutableSetOf(KAPT_KOTLIN_GENERATED)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String?> {
        return mutableSetOf(Injectable::class.qualifiedName, InjectableProvider::class.qualifiedName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        val directory = processingEnv
            .options[KAPT_KOTLIN_GENERATED]
            ?.let { File(it) }
            ?.apply { mkdir() }
            ?: throw PopKornException("Can't find the target directory for generated Kotlin files.")
        mainGenerator = MainGenerator(directory, processingEnv.filer, processingEnv.typeUtils, processingEnv.elementUtils, Logger(processingEnv.messager))
        mainGenerator.init()
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        if (roundEnv.errorRaised()) return false
        return try {
            mainGenerator.process(roundEnv)
            if (roundEnv.processingOver()) mainGenerator.end()
            true
        } catch (e: Throwable) {
            mainGenerator.logger.error(e.message ?: "")
            false
        }
    }

}
