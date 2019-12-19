package cc.popkorn

import cc.popkorn.core.Injector
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFails

/**
 * Class to test runtime injections
 *
 * @author Pau Corbella
 * @since 1.3.0
 */
@RunWith(JUnit4::class)
internal class RuntimeTests : PopKornTest() {
    interface I1
    interface I2
    abstract class C1 : I1
    class C2 : C1(), I2

    @Test
    fun testAddInjectableClass() {
        val injector = Injector(true)

        val c = C2()
        injector.addInjectable(c)
        assertEquals(injector.inject(C2::class), c)
        assertFails { injector.inject(C1::class) }
        assertFails { injector.inject(I1::class) }
        assertFails { injector.inject(I2::class) }

        injector.removeInjectable(C2::class)
        assertFails { injector.inject(C2::class) }

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }

    @Test
    fun testAddInjectableAbstractClass() {
        val injector = Injector(true)

        val c = C2()
        injector.addInjectable(c, C1::class)
        assertEquals(injector.inject(C1::class), c)
        assertFails { injector.inject(C2::class) }
        assertFails { injector.inject(I1::class) }
        assertFails { injector.inject(I2::class) }

        injector.removeInjectable(C1::class)
        assertFails { injector.inject(C1::class) }

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }


    @Test
    fun testAddInjectableDirectInterface() {
        val injector = Injector(true)

        val c = C2()
        injector.addInjectable(c, I2::class)
        assertEquals(injector.inject(I2::class), c)
        assertFails { injector.inject(C2::class) }
        assertFails { injector.inject(C1::class) }
        assertFails { injector.inject(I1::class) }

        injector.removeInjectable(I2::class)
        assertFails { injector.inject(I2::class) }

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }


    @Test
    fun testAddInjectableFarInterface() {
        val injector = Injector(true)

        val c = C2()
        injector.addInjectable(c, I1::class)
        assertEquals(injector.inject(I1::class), c)
        assertFails { injector.inject(C2::class) }
        assertFails { injector.inject(C1::class) }
        assertFails { injector.inject(I2::class) }

        injector.removeInjectable(I1::class)
        assertFails { injector.inject(I1::class) }

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }


    @Test
    fun testAddInjectableTwoInterface() {
        val injector = Injector(true)

        val c = C2()
        injector.addInjectable(c, I1::class)
        injector.addInjectable(c, I2::class)
        assertEquals(injector.inject(I1::class), c)
        assertEquals(injector.inject(I2::class), c)
        assertFails { injector.inject(C2::class) }
        assertFails { injector.inject(C1::class) }

        injector.removeInjectable(I1::class)
        assertFails { injector.inject(I1::class) }
        assertEquals(injector.inject(I2::class), c)

        injector.removeInjectable(I2::class)
        assertFails { injector.inject(I2::class) }

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }


    @Test
    fun testAddInjectableClassAndInterface() {
        val injector = Injector(true)

        val c = C2()
        injector.addInjectable(c, C2::class)
        injector.addInjectable(c, I1::class)
        assertEquals(injector.inject(C2::class), c)
        assertEquals(injector.inject(I1::class), c)
        assertFails { injector.inject(C1::class) }
        assertFails { injector.inject(I2::class) }

        injector.removeInjectable(I1::class)
        assertFails { injector.inject(I1::class) }
        assertEquals(injector.inject(C2::class), c)

        injector.removeInjectable(C2::class)
        assertFails { injector.inject(C2::class) }

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)

    }


    @Test
    fun testAddInjectableClassWithEnvironments() {
        testInjectAs(C2::class)
    }

    @Test
    fun testAddInjectableAbstractClassWithEnvironments() {
        testInjectAs(C1::class)
    }


    @Test
    fun testAddInjectableDirectInterfaceWithEnvironments() {
        testInjectAs(I2::class)
    }

    @Test
    fun testAddInjectableFarInterfaceWithEnvironments() {
        testInjectAs(I1::class)
    }


    private fun testInjectAs(clazz: KClass<out Any>) {
        val injector = Injector(true)
        val c0 = C2()
        val c1 = C2()
        val c2 = C2()
        val c3 = C2()

        injector.addInjectable(c0, clazz)
        injector.addInjectable(c1, clazz, "1")
        injector.addInjectable(c2, clazz, "2")
        injector.addInjectable(c3, clazz, "3")

        assertEquals(injector.inject(clazz), c0)
        assertEquals(injector.inject(clazz, "1"), c1)
        assertEquals(injector.inject(clazz, "2"), c2)
        assertEquals(injector.inject(clazz, "3"), c3)

        injector.removeInjectable(clazz, "1")
        assertEquals(injector.inject(clazz), c0)
        assertEquals(injector.inject(clazz, "1"), c0)
        assertEquals(injector.inject(clazz, "2"), c2)
        assertEquals(injector.inject(clazz, "3"), c3)

        injector.removeInjectable(clazz, "2")
        assertEquals(injector.inject(clazz), c0)
        assertEquals(injector.inject(clazz, "1"), c0)
        assertEquals(injector.inject(clazz, "2"), c0)
        assertEquals(injector.inject(clazz, "3"), c3)

        injector.removeInjectable(clazz)
        assertFails { injector.inject(clazz) }
        assertFails { injector.inject(clazz, "1") }
        assertFails { injector.inject(clazz, "2") }
        assertEquals(injector.inject(clazz, "3"), c3)

        injector.removeInjectable(clazz, "3")
        assertFails { injector.inject(clazz) }
        assertFails { injector.inject(clazz, "1") }
        assertFails { injector.inject(clazz, "2") }
        assertFails { injector.inject(clazz, "3") }

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }


    @Test
    fun testPurge() {
        val injector = Injector(true)

        injector.addInjectable(C2())
        injector.addInjectable(C2(), C2::class, "4")
        injector.addInjectable(C2(), C1::class)
        injector.addInjectable(C2(), C1::class, "4")
        injector.addInjectable(C2(), I2::class, "4")
        injector.addInjectable(C2(), I1::class, "6")

        assertEquals(4, injector.instances.size)
        assertEquals(2, injector.resolvers.size)

        injector.purge()

        //Purge must not affect to runtime injections
        assertEquals(4, injector.instances.size)
        assertEquals(2, injector.resolvers.size)
    }


    @Test
    fun testReset() {
        val injector = Injector(true)

        injector.addInjectable(C2())
        injector.addInjectable(C2(), C2::class, "4")
        injector.addInjectable(C2(), C1::class)
        injector.addInjectable(C2(), C1::class, "4")
        injector.addInjectable(C2(), I2::class, "4")
        injector.addInjectable(C2(), I1::class, "6")

        assertEquals(4, injector.instances.size)
        assertEquals(2, injector.resolvers.size)

        injector.reset()

        assertEquals(0, injector.instances.size)
        assertEquals(0, injector.resolvers.size)
    }


}