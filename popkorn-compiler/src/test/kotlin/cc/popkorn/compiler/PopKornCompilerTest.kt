package cc.popkorn.compiler

import cc.popkorn.compiler.utils.JavaClass
import com.google.testing.compile.Compilation
import com.google.testing.compile.CompilationSubject
import com.google.testing.compile.Compiler
import com.google.testing.compile.JavaFileObjects
import org.apache.commons.io.FileUtils
import java.io.File

/**
 * Parent class of all compilation tests
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
abstract class PopKornCompilerTest {
    private val projectFolder = System.getProperty("user.dir")
    private val generatedFolder = "$projectFolder/build/generated/source/kaptKotlin/test"

    fun assertCompileSuccess(vararg classes: JavaClass) {
        try {
            val compilation = compile(*classes)
            CompilationSubject.assertThat(compilation).succeeded()
            assertFiles(*classes)
        } finally {
            clean()
        }
    }

    fun assertCompileFail(vararg classes: JavaClass) {
        try {
            val compilation = compile(*classes)
            CompilationSubject.assertThat(compilation).failed()
        } catch (e: Exception) {
            // Assertion OK
        } finally {
            clean()
        }
    }

    private fun compile(vararg classes: JavaClass): Compilation {
        val files = classes.map { JavaFileObjects.forSourceString(it.qualifiedName(), it.construct()) }

        return Compiler.javac()
            .withProcessors(PopKornCompiler())
            .withOptions("-A${PopKornCompiler.KAPT_KOTLIN_GENERATED}=$generatedFolder")
            .compile(files)
    }

    private fun clean() {
        File(generatedFolder).listFiles()?.forEach { FileUtils.forceDelete(it) }
    }

    private fun assertFiles(vararg classes: JavaClass) {
        classes.mapNotNull { it.getGeneratedFile()?.replace(".", "/") }
            .map { File("$generatedFolder/$it.kt") }
            .takeIf { files -> files.all { it.exists() } }
            ?: throw RuntimeException("Some providers/resolvers have not been generated")
    }
}
