package cc.popkorn.compiler

import cc.popkorn.compiler.utils.JavaClass
import cc.popkorn.compiler.utils.JavaMethod
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Class to test compilation of classes annotated with @InjectableProvider
 *
 * @author Pau Corbella
 * @since 1.2.0
 */
@RunWith(JUnit4::class)
class ProvidedInjectableTests : PopKornCompilerTest() {

    @Test
    fun testPrivateClassInjectable(){
        val test = JavaClass().modifiers("private").injectableProvider()
        assertCompileFail(test)
    }

    @Test
    fun testAbstractClassInjectable(){
        val test = JavaClass().modifiers("public", "abstract").injectableProvider()
        assertCompileFail(test)
    }

    @Test
    fun testInterfaceInjectable(){
        val test = JavaClass("interface").modifiers("public").injectableProvider()
        assertCompileFail(test)
    }


    @Test
    fun testPublicClassInjectable(){
        val inject = JavaClass().modifiers("public")
        val test = JavaClass().modifiers("public")
            .injectableProvider()
            .method(JavaMethod("testFun").modifiers("public").returns(inject.qualifiedName()))

        assertCompileSuccess(inject, test)
    }


    @Test
    fun testPublicClassWithAlias(){
        val inject = JavaClass().modifiers("public")
        val test = JavaClass().modifiers("public")
            .injectableProvider(alias = "alias")
            .method(JavaMethod("testFun").modifiers("public").returns(inject.qualifiedName()))

        assertCompileSuccess(inject, test)

        //If already have this alias, should fail
        val test2 = JavaClass().modifiers("public")
            .injectableProvider(alias = "alias")
            .method(JavaMethod("testFun").modifiers("public").returns(inject.qualifiedName()))
        assertCompileFail(inject, test, test2)
    }


    @Test
    fun testPublicClassWithEnvironments(){
        val inject = JavaClass().modifiers("public")
        val test = JavaClass().modifiers("public")
            .injectableProvider()
            .forEnvironments("1", "2")
            .method(JavaMethod("testFun").modifiers("public").returns(inject.qualifiedName()))

        assertCompileSuccess(inject, test)
    }


    @Test
    fun testPublicClassWithPrimitiveType(){
        val test = JavaClass().modifiers("public")
            .injectableProvider()
            .method(JavaMethod("testFun").modifiers("public").returns("int", "3"))

        assertCompileSuccess(test)
    }

    @Test
    fun testPublicClassWithDifferentReturnTypes(){
        val type1 = JavaClass().modifiers("public")
        val type2 = JavaClass().modifiers("public")

        val test = JavaClass().modifiers("public")
            .injectableProvider()
            .method(JavaMethod("testFun").modifiers("public").returns(type1.qualifiedName()))
            .method(JavaMethod("testFun2").modifiers("public").returns(type2.qualifiedName()))

        assertCompileFail(type1, type2, test)

    }

}