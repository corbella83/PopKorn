package cc.popkorn

import cc.popkorn.core.Injector
import cc.popkorn.data.*
import kotlin.test.*

/**
 * Class to test providers
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
internal class ProviderTests : PopKornTest() {

    @Test
    fun testClassWithoutProvider() {
        testClassWithoutProvider(randEnvironment())  // Custom Environment
        testClassWithoutProvider(null)      // Default Environment
    }


    private fun testClassWithoutProvider(environment: String?) {
        val factory = Injector(TestResolverPool(), TestProviderPool())

        assertFails { factory.inject(TestClassNoProvider::class, environment) }

        factory.assertNumberInstances(0)
    }


    @Test
    fun testClassByApp() {
        testClassByApp(randEnvironment())  // Custom Environment
        testClassByApp(null)      // Default Environment
    }

    private fun testClassByApp(environment: String?) {
        val factory = Injector(TestResolverPool(), TestProviderPool())

        val inject = factory.inject(TestClassByApp::class, environment)
        assertEquals(inject.value, environment)

        //If injected again, should return the same instance
        val inject2 = factory.inject(TestClassByApp::class, environment)
        assertTrue(inject === inject2)

        factory.assertNumberInstances(1)
        factory.assertNumberInstancesForClass(TestClassByApp::class, 1)
    }


    @Test
    fun testClassByUse() {
        testClassByUse(randEnvironment())  // Custom Environment
        testClassByUse(null)      // Default Environment
    }

    private fun testClassByUse(environment: String?) {
        val factory = Injector(TestResolverPool(), TestProviderPool())

        val inject = factory.inject(TestClassByUse::class, environment)
        assertEquals(inject.value, environment)

        //If injected again, should return the same instance
        val inject2 = factory.inject(TestClassByUse::class, environment)
        assertTrue(inject === inject2)

        factory.assertNumberInstances(1)
        factory.assertNumberInstancesForClass(TestClassByUse::class, 1)
    }


    @Test
    fun testClassByNew() {
        testClassByNew(randEnvironment())  // Custom Environment
        testClassByNew(null)      // Default Environment
    }

    private fun testClassByNew(environment: String?) {
        val factory = Injector(TestResolverPool(), TestProviderPool())

        val inject = factory.inject(TestClassByNew::class, environment)
        assertEquals(inject.value, environment)

        //If injected again, should return the same new instance
        val inject2 = factory.inject(TestClassByNew::class, environment)
        assertTrue(inject !== inject2)

        factory.assertNumberInstances(1)
        factory.assertNumberInstancesForClass(TestClassByNew::class, 0)
    }


    @Test
    fun testClassByManual() {
        testClassByManual(randEnvironment())  // Custom Environment
        testClassByManual(null)      // Default Environment
    }


    private fun testClassByManual(environment: String?) {
        val factory = Injector(TestResolverPool(), TestProviderPool())
        val instance = TestClassNoProvider(environment)
        factory.addInjectable(instance, environment = environment)
        factory.assertNumberInstances(1)
        factory.assertNumberInstancesForClass(TestClassNoProvider::class, 1)

        val inject = factory.inject(TestClassNoProvider::class, environment)
        assertEquals(inject.value, environment)

        //If injected again, should return the same instance
        val inject2 = factory.inject(TestClassNoProvider::class, environment)
        assertTrue(inject === inject2)

        if (environment == null) { //If try to inject another environment, will return the default instance
            val secondaryEnvironment = randEnvironment()
            val inject3 = factory.inject(TestClassNoProvider::class, secondaryEnvironment)
            assertTrue(inject === inject3)
        } else { //If try to inject the default environment or any other, will fail
            val secondaryEnvironment = "${environment}_secondary"
            assertFails { factory.inject(TestClassNoProvider::class, null) }
            assertFails { factory.inject(TestClassNoProvider::class, secondaryEnvironment) }
        }

        //Once removed the instance, should fail
        factory.removeInjectable(instance::class, environment)
        assertFails { factory.inject(TestClassNoProvider::class, environment) }

        factory.assertNumberInstances(0)
    }


    @Test
    fun testClassByManualTogether() {
        val factory = Injector(TestResolverPool(), TestProviderPool())
        val environment = randEnvironment()
        val instanceDef = TestClassNoProvider(null)
        val instanceEnv = TestClassNoProvider(environment)

        factory.addInjectable(instanceDef, environment = null)
        factory.addInjectable(instanceEnv, environment = environment)
        factory.assertNumberInstances(1)
        factory.assertNumberInstancesForClass(TestClassNoProvider::class, 2)

        val inject = factory.inject(TestClassNoProvider::class, null)
        assertNull(inject.value)

        val inject2 = factory.inject(TestClassNoProvider::class, environment)
        assertEquals(inject2.value, environment)

        //If any other environment must return the default one
        val secondaryEnvironment = "${environment}_secondary"
        val inject3 = factory.inject(TestClassNoProvider::class, secondaryEnvironment)
        assertTrue(inject === inject3)

        factory.removeInjectable(instanceDef::class)
        factory.assertNumberInstances(1)
        factory.assertNumberInstancesForClass(TestClassNoProvider::class, 1)

        //If no default one, the same injection must crash
        assertFails { factory.inject(TestClassNoProvider::class, secondaryEnvironment) }
    }


    @Test
    fun testClassByManualWithExistingProviderFile() {
        val factory = Injector(TestResolverPool(), TestProviderPool())
        val environment = randEnvironment()

        val instance = TestClassByApp(environment)
        assertFails { factory.addInjectable(instance, environment = environment) }

        factory.assertNumberInstances(0)
    }


    @Test
    fun testClassByManualWithExistingProviderInstance() {
        val factory = Injector(TestResolverPool(), TestProviderPool())
        val environment = randEnvironment()

        val inject = factory.inject(TestClassByApp::class, null)
        assertNull(inject.value)

        val instance = TestClassByApp(environment)
        assertFails { factory.addInjectable(instance, environment = environment) }

        factory.assertNumberInstances(1)
    }


    @Test
    fun testReset() {
        val factory = Injector(TestResolverPool(), TestProviderPool())
        val environment = randEnvironment()

        val inject = factory.inject(TestClassByApp::class, null)
        assertNull(inject.value)
        factory.assertNumberInstances(1)
        factory.assertNumberInstancesForClass(TestClassByApp::class, 1)

        factory.addInjectable(TestClassNoProvider(null), environment = null)
        factory.addInjectable(TestClassNoProvider(environment), environment = environment)
        factory.assertNumberInstances(2)
        factory.assertNumberInstancesForClass(TestClassNoProvider::class, 2)

        factory.reset()

        factory.assertNumberInstances(0)
    }

}