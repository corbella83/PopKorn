package cc.popkorn.compiler

import cc.popkorn.PROVIDER_SUFFIX
import cc.popkorn.RESOLVER_SUFFIX
import com.google.testing.compile.Compilation
import com.google.testing.compile.CompilationSubject
import com.google.testing.compile.Compiler
import com.google.testing.compile.JavaFileObjects
import org.apache.commons.io.FileUtils
import java.io.File

abstract class PopKornCompilerTest {
    private val packDash = "cc/popkorn/compiler"
    private val projectFolder = System.getProperty("user.dir")
    private val generatedFolder = "$projectFolder/build/generated/source/kaptKotlin/test"


    fun assertCompileSuccess(vararg classes:ClassCreator){
        try {
            val compilation = compile(*classes)
            CompilationSubject.assertThat(compilation).succeeded()
            assertFiles(*classes)
        }finally {
            clean()
        }
    }

    fun assertCompileFail(vararg classes:ClassCreator){
        try{
            val compilation = compile(*classes)
            CompilationSubject.assertThat(compilation).failed()
        }catch (e:Exception){
            // assertion ok
        } finally {
            clean()
        }
    }



    private fun compile(vararg classes:ClassCreator) : Compilation{
        val files = classes.map { JavaFileObjects.forSourceString(it.name(), it.construct()) }

        return Compiler.javac()
            .withProcessors(PopKornCompiler())
            .withOptions("-A${PopKornCompiler.KAPT_KOTLIN_GENERATED}=$generatedFolder")
            .compile(files)
    }

    private fun clean(){
        File(generatedFolder).listFiles()?.forEach { FileUtils.forceDelete(it) }
    }


    private fun assertFiles(vararg classes:ClassCreator){
        val listFiles = File("$generatedFolder/$packDash").listFiles()?.map { it.nameWithoutExtension } ?: throw RuntimeException("No generated files")
        classes.map {
            if (it.isPublicClass()){
                "${it.simpleName()}_$PROVIDER_SUFFIX"
            }else{
                "${it.simpleName()}_$RESOLVER_SUFFIX"
            }
        }.forEach {
            if (!listFiles.contains(it)) throw RuntimeException("Some providers/resolvers have not been generated")
        }

    }


}