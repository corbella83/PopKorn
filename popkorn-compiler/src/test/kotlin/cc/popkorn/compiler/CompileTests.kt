package cc.popkorn.compiler

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CompileTests : PopKornCompilerTest() {

    @Test
    fun testPrivateClassInjectable(){
        val test = ClassCreator(type = "private class")
        assertCompileFail(test)
    }

    @Test
    fun testAbstractClassInjectable(){
        val test = ClassCreator(type = "public abstract class")
        assertCompileFail(test)
    }

    @Test
    fun testInterfaceInjectable(){
        val test = ClassCreator(type = "public interface")
        assertCompileFail(test)
    }


    @Test
    fun testPublicClassInjectable(){
        val test = ClassCreator()
        assertCompileSuccess(test)
    }


    @Test
    fun testPublicClassWithAlias(){
        val test = ClassCreator(injectableAlias = "alias")
        assertCompileSuccess(test)

        //If already have this alias, should fail
        val test2 = ClassCreator(injectableAlias = "alias")
        assertCompileFail(test, test2)
    }


    @Test
    fun testPublicClassWithEnvironments(){
        val test = ClassCreator(environments = arrayListOf("1", "2"))
        assertCompileSuccess(test)
    }


    @Test
    fun testPublicClassWithSuper(){
        val test = ClassCreator(type="public interface", injectable = false)
        val test2 = ClassCreator(interfaces = arrayListOf(test.name()))
        assertCompileSuccess(test, test2)
    }


    @Test
    fun test2PublicClassWithSameSuper(){
        val test = ClassCreator(type="public interface", injectable = false)
        val test2 = ClassCreator(interfaces = arrayListOf(test.name()))
        val test3 = ClassCreator(interfaces = arrayListOf(test.name()))
        assertCompileFail(test, test2, test3)
    }

    @Test
    fun test2PublicClassWithSameSuperWithoutDefaultEnvironments(){
        val test = ClassCreator(type="public interface", injectable = false)
        val test2 = ClassCreator(interfaces = arrayListOf(test.name()), environments = arrayListOf("4"))
        val test3 = ClassCreator(interfaces = arrayListOf(test.name()), environments = arrayListOf("4"))
        assertCompileFail(test, test2, test3)
    }


    @Test
    fun test2PublicClassWithSameSuperWithDifferentEnvironments(){
        val test = ClassCreator(type="public interface", injectable = false)
        val test2 = ClassCreator(interfaces = arrayListOf(test.name()), environments = arrayListOf("4"))
        val test3 = ClassCreator(interfaces = arrayListOf(test.name()))
        assertCompileSuccess(test, test2, test3)
    }



}