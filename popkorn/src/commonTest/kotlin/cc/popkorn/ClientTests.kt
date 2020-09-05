package cc.popkorn

import cc.popkorn.core.Injector
import cc.popkorn.data.*
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertSame

/**
 * Class to test injectable classes end to end
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
internal class ClientTests : PopKornTest() {

    @Test
    fun testWhole() {
        val tmp = TestClassNoProvider(randEnvironment())
        val injector = Injector(TestResolverPool(), TestProviderPool())
        injector.addInjectable(tmp)

        val classApp = injector.inject<TestClassByApp>("app")
        val classUse = injector.inject<TestClassByUse>("use")
        val classNew = injector.inject<TestClassByNew>("new")
        val classManual = injector.inject<TestClassNoProvider>("some")

        val interfaceApp = injector.inject<TestInterface>("app")
        val interfaceUse = injector.inject<TestInterface>("use")
        val interfaceNew = injector.injectNullable(TestInterface::class, "new")

        assertSame(classApp, interfaceApp)
        assertSame(classUse, interfaceUse)
        assertNotSame(classNew, interfaceNew)
        assertNotNull(classManual)

        injector.removeInjectable(tmp::class)

        injector.reset()

    }

}