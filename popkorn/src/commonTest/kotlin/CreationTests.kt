package cc.popkorn

import cc.popkorn.core.Injector
import cc.popkorn.core.exceptions.AssistedNotFoundException
import cc.popkorn.data.*
import kotlin.test.*

/**
 * Class to test creations
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
internal class CreationTests : PopKornTest() {

    @Test
    fun testPlainOk() {
        val injector = Injector(TestResolverPool(), TestProviderPool())

        val instance1 = injector.create(TestClassByApp::class)
        val instance2 = injector.create(TestClassByApp::class)
        assertTrue(instance1 !== instance2)

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }

    @Test
    fun testOverrideOk() {
        val injector = Injector(TestResolverPool(), TestProviderPool())

        val obj1 = injector.create(TestCascadeClass::class)
        val obj2 = injector.create(TestCascadeClass::class)
        assertSame(obj1.param1, obj2.param1)

        val use = TestClassByApp(null)
        val obj3 = injector.create(TestCascadeClass::class) { override(use) }

        assertSame(use, obj3.param1)
        assertNotSame(obj1.param1, obj3.param1)

        assertEquals(2, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }

    @Test
    fun testWithParamsNotAssisted() {
        val injector = Injector(TestResolverPool(), TestProviderPool())

        assertFailsWith<AssistedNotFoundException> { injector.create(TestClassByNewAssisted::class) }

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }

    @Test
    fun testWithParamsMissingAssisted() {
        val injector = Injector(TestResolverPool(), TestProviderPool())

        assertFailsWith<AssistedNotFoundException> {
            injector.create(TestClassByNewAssisted::class) {
                assist(34f)
            }
        }

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }

    @Test
    fun testWithParamsOk() {
        val injector = Injector(TestResolverPool(), TestProviderPool())

        val obj1 = injector.create(TestClassByNewAssisted::class) {
            assist("test")
            assist(34)
        }
        assertEquals("test", obj1.param1)
        assertEquals(34, obj1.param2)

        assertEquals(0, injector.resolvers.size)

        val obj2 = injector.create(TestInterface::class, "assist1") {
            assist("test")
            assist(34)
        }
        assertEquals(obj1::class, obj2::class)
        assertTrue(obj1 !== obj2)

        assertEquals(0, injector.instances.size)
        assertEquals(1, injector.resolvers.size)
    }

    @Test
    fun testWithParamsSameTypeParamsNotAssisted() {
        val injector = Injector(TestResolverPool(), TestProviderPool())

        assertFailsWith<AssistedNotFoundException> { injector.create(TestClassByNewAssisted2::class) }

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }

    @Test
    fun testWithParamsSameTypeOk() {
        val injector = Injector(TestResolverPool(), TestProviderPool())

        val obj1 = injector.create(TestClassByNewAssisted2::class) {
            assist("test", "env1")
            assist("more", "env2")
        }
        assertEquals("test", obj1.param1)
        assertEquals("more", obj1.param2)

        assertEquals(0, injector.resolvers.size)

        val obj2 = injector.create(TestInterface::class, "assist2") {
            assist("test", "env1")
            assist("more", "env2")
        }
        assertEquals(obj1::class, obj2::class)
        assertTrue(obj1 !== obj2)

        assertEquals(0, injector.instances.size)
        assertEquals(1, injector.resolvers.size) // Resolvers have to be cached
    }
}
