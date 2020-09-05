package cc.popkorn

import cc.popkorn.core.Injector
import cc.popkorn.data.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

/**
 * Class to test resolvers
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
internal class ResolverTests : PopKornTest() {
    private val availableEnvironments = hashMapOf(
        null to TestClassByApp::class,
        "app" to TestClassByApp::class,
        "use" to TestClassByUse::class,
        "new" to TestClassByNew::class
    )

    @Test
    fun testInterfaceWithoutResolver() {
        testInterfaceWithoutResolver(randEnvironment())  // Custom Environment
        testInterfaceWithoutResolver(null)      // Default Environment
    }

    private fun testInterfaceWithoutResolver(environment: String?) {
        val factory = Injector(TestResolverPool(), TestProviderPool())
        assertFails { factory.inject(TestInterfaceNoResolver::class, environment) }

    }


    @Test
    fun testInterface() {
        val factory = Injector(TestResolverPool(), TestProviderPool())

        //For every correct environment, should return the correct class
        availableEnvironments.forEach {
            val inject = factory.inject(TestInterface::class, it.key)
            assertEquals(inject.value, it.key)
            assertEquals(inject::class, it.value)
        }

        //If new environment, should return the default implementation with the new environment
        val newEnvironment = "newOne"
        val inject2 = factory.inject(TestInterface::class, newEnvironment)
        assertEquals(inject2.value, newEnvironment)
        assertEquals(inject2::class, TestClassByApp::class)

        factory.assertNumberInstances(3)
        factory.assertNumberInstancesForClass(
            TestClassByApp::class,
            3
        ) // One for default, one for "app" and one for "newOne"
        factory.assertNumberInstancesForClass(TestClassByUse::class, 1) // One for "use"
        factory.assertNumberInstancesForClass(TestClassByNew::class, 0) // BY_NEW never have instances
    }


}