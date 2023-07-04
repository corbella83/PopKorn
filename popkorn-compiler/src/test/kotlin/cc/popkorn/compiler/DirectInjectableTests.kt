package cc.popkorn.compiler

import cc.popkorn.compiler.utils.JavaClass
import cc.popkorn.compiler.utils.JavaParam
import cc.popkorn.core.Scope
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Class to test compilation of classes annotated with @Injectable
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
@RunWith(JUnit4::class)
class DirectInjectableTests : PopKornCompilerTest() {

    @Test
    fun testPrivateClassInjectable() {
        val test = JavaClass().modifiers("private").injectable()
        assertCompileFail(test)
    }

    @Test
    fun testAbstractClassInjectable() {
        val test = JavaClass().modifiers("public", "abstract").injectable()
        assertCompileFail(test)
    }

    @Test
    fun testInterfaceInjectable() {
        val test = JavaClass("interface").modifiers("public").injectable()
        assertCompileFail(test)
    }

    @Test
    fun testPublicClassInjectable() {
        val test = JavaClass().modifiers("public").injectable()
        assertCompileSuccess(test)
    }

    @Test
    fun testPublicClassWithAlias() {
        val test = JavaClass().modifiers("public").injectable(alias = "alias")
        assertCompileSuccess(test)

        // If already have this alias, should fail
        val test2 = JavaClass().modifiers("public").injectable(alias = "alias")
        assertCompileFail(test, test2)
    }

    @Test
    fun testPublicClassWithEnvironments() {
        val test = JavaClass().modifiers("public").injectable().forEnvironments("1", "2")
        assertCompileSuccess(test)
    }

    @Test
    fun testPublicClassWithSuper() {
        val test = JavaClass("interface").modifiers("public")
        val test2 = JavaClass().modifiers("public").injectable().implements(test.qualifiedName())
        assertCompileSuccess(test, test2)
    }

    @Test
    fun testPublicClassWithSameSuper() {
        val test = JavaClass("interface").modifiers("public")
        val test2 = JavaClass().modifiers("public").injectable().implements(test.qualifiedName())
        val test3 = JavaClass().modifiers("public").injectable().implements(test.qualifiedName())
        assertCompileFail(test, test2, test3)
    }

    @Test
    fun testPublicClassWithSameSuperWithoutDefaultEnvironments() {
        val test = JavaClass("interface").modifiers("public")
        val test2 = JavaClass().modifiers("public").injectable().implements(test.qualifiedName()).forEnvironments("4")
        val test3 = JavaClass().modifiers("public").injectable().implements(test.qualifiedName()).forEnvironments("4")
        assertCompileFail(test, test2, test3)
    }

    @Test
    fun testPublicClassWithSameSuperWithDifferentEnvironments() {
        val test = JavaClass("interface").modifiers("public")
        val test2 = JavaClass().modifiers("public").injectable().implements(test.qualifiedName()).forEnvironments("4")
        val test3 = JavaClass().modifiers("public").injectable().implements(test.qualifiedName())
        assertCompileSuccess(test, test2, test3)
    }

    @Test
    fun testAssistedOk() {
        val test = JavaClass().modifiers("public")
        val param = JavaParam("id", "int").assisted()
        val test2 = JavaClass().modifiers("public").injectable(Scope.BY_NEW).constructor(null, param)
        assertCompileSuccess(test, test2)
    }

    @Test
    fun testAssistedInvalidScope() {
        testAssistedInvalidScope(Scope.BY_APP)
        testAssistedInvalidScope(Scope.BY_USE)
        testAssistedInvalidScope(Scope.BY_HOLDER)
    }

    private fun testAssistedInvalidScope(scope: Scope) {
        val test = JavaClass().modifiers("public")
        val param = JavaParam("id", "int").assisted()
        val test2 = JavaClass().modifiers("public").injectable(scope).constructor(null, param)
        assertCompileFail(test, test2)
    }
}
