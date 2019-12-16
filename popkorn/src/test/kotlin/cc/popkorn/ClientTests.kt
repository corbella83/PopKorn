package cc.popkorn

import cc.popkorn.core.Injector
import cc.popkorn.data.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * Class to test injectable classes end to end
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
@RunWith(JUnit4::class)
internal class ClientTests : PopKornTest() {

    @Test
    fun testWhole() {
        val tmp = TestClassNoProvider(randEnvironment())
        getPopKornController().addInjectable(tmp)

        val classApp = TestClassByApp::class.inject("app")
        val classUse = TestClassByUse::class.inject("use")
        val classNew = inject<TestClassByNew>("new")
        val classManual = inject<TestClassNoProvider>("some")

        val interfaceApp = inject<TestInterface>("app")
        val interfaceUse = inject<TestInterface>("use")
        val interfaceNew = inject<TestInterface>("new")

        assert(classApp === interfaceApp)
        assert(classUse === interfaceUse)
        assert(classNew !== interfaceNew)
        assertNotNull(classManual)

        getPopKornController().removeInjectable(tmp::class)

        getPopKornController().reset()

    }

}