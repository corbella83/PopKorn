package cc.popkorn

import cc.popkorn.core.Injector
import cc.popkorn.core.exceptions.AssistedNotFoundException
import cc.popkorn.data.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Class to test creations
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
internal class AssistedTests : PopKornTest() {

    @Test
    fun testNoParameters() {
        val injector = Injector(TestResolverPool(), TestProviderPool())

        val instance1 = injector.create(TestClassByApp::class)
        val instance2 = injector.create(TestClassByApp::class)
        assertTrue(instance1 !== instance2)

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }

    @Test
    fun testParam2NotProvided() {
        val injector = Injector(TestResolverPool(), TestProviderPool())

        assertFailsWith<AssistedNotFoundException> { injector.create(TestAssistedClass::class) }

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }

    @Test
    fun testParamsNotProvided() {
        val injector = Injector(TestResolverPool(), TestProviderPool())

        assertFailsWith<AssistedNotFoundException> {
            injector.create(TestAssistedClass::class) {
                assist(34f)
            }
        }

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }

    @Test
    fun testMissingParams() {
        val injector = Injector(TestResolverPool(), TestProviderPool())

        assertFailsWith<AssistedNotFoundException> {
            injector.create(TestAssistedClass::class) {
                assist(34f)
            }
        }

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }

    @Test
    fun testParamsOk() {
        val injector = Injector(TestResolverPool(), TestProviderPool())

        val obj1 = injector.create(TestAssistedClass::class) {
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
    fun testParamsMissingEnvironment() {
        val injector = Injector(TestResolverPool(), TestProviderPool())

        assertFailsWith<AssistedNotFoundException> { injector.create(TestAssistedClass2::class) }
    }

    @Test
    fun testParamsWithEnvironments() {
        val injector = Injector(TestResolverPool(), TestProviderPool())

        val obj1 = injector.create(TestAssistedClass2::class) {
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
