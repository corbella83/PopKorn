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

        val classApp = injector.inject(TestClassByApp::class, "app")
        val classUse = injector.inject(TestClassByUse::class, "use")
        val classNew = injector.inject(TestClassByNew::class, "new")
        val classManual = injector.inject(TestClassNoProvider::class, "some")

        val interfaceApp = injector.inject(TestInterface::class, "app")
        val interfaceUse = injector.inject(TestInterface::class, "use")
        val interfaceNew = injector.injectOrNull(TestInterface::class, "new")

        assertSame(classApp, interfaceApp)
        assertSame(classUse, interfaceUse)
        assertNotSame(classNew, interfaceNew)
        assertNotNull(classManual)

        injector.removeInjectable(tmp::class)

        injector.reset()
    }
}
