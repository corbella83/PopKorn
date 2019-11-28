package cc.popkorn.compiler

import cc.popkorn.annotations.Exclude
import cc.popkorn.annotations.Injectable
import cc.popkorn.annotations.InjectableProvider
import cc.popkorn.compiler.generators.PopKornGenerator
import cc.popkorn.compiler.utils.*
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement


/**
 * AbstractProcessor to process all PopKorn annotations and generates the necessary source code files
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class PopKornCompiler : AbstractProcessor() {
    private lateinit var popKornGenerator: PopKornGenerator

    companion object {
        const val KAPT_KOTLIN_GENERATED = "kapt.kotlin.generated"
    }

    override fun getSupportedOptions(): MutableSet<String> {
        return mutableSetOf(KAPT_KOTLIN_GENERATED)
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String?> {
        return mutableSetOf(Injectable::class.qualifiedName, InjectableProvider::class.qualifiedName, Exclude::class.qualifiedName)
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
        val logger = Logger(processingEnv.messager)
        popKornGenerator = PopKornGenerator(directory, processingEnv.filer, processingEnv.typeUtils, processingEnv.elementUtils, logger)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        popKornGenerator.process(roundEnv)
        return true
    }

}