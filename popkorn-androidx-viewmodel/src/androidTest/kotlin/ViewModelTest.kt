package cc.popkorn.androidx.viewModel

import cc.popkorn.ParametersFactory
import cc.popkorn.androidx.viewModel.data.TestViewModel
import cc.popkorn.androidx.viewModel.data.TestViewModelProviderPool
import cc.popkorn.androidx.viewModel.data.TestViewModelResolverPool
import cc.popkorn.androidx.viewModel.data.TestViewModelWithParams
import cc.popkorn.core.Injector
import cc.popkorn.core.exceptions.AssistedNotFoundException
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class ViewModelTest {

    @Test
    fun `Test ViewModel injection`() {
        val injector = Injector(TestViewModelResolverPool(), TestViewModelProviderPool())
        injector.addInjectable(TestViewModel())

        val testViewModel = injector.inject(TestViewModel::class)

        assertNotNull(testViewModel)

        injector.removeInjectable(testViewModel::class)

        injector.reset()
    }

    @Test
    fun `Test ViewModel where all params are missing`() {
        val injector = Injector(TestViewModelResolverPool(), TestViewModelProviderPool())

        assertFailsWith<AssistedNotFoundException> { injector.create(TestViewModelWithParams::class) }
    }

    @Test
    fun `Test ViewModel where one param is missing`() {
        val injector = Injector(TestViewModelResolverPool(), TestViewModelProviderPool())

        val params = ParametersFactory.Builder().apply {
            add(34)
        }.build()

        assertFailsWith<AssistedNotFoundException> { injector.create(TestViewModelWithParams::class, params) }
    }

    @Test
    fun `Test ViewModel with params`() {
        val params = ParametersFactory.Builder().apply {
            add("test")
            add(34)
        }.build()

        val injector = Injector(TestViewModelResolverPool(), TestViewModelProviderPool())

        val testViewModelWithParams = injector.create(TestViewModelWithParams::class, params)

        assertNotNull(testViewModelWithParams)
        assertEquals("test", testViewModelWithParams.name)
        assertEquals(34, testViewModelWithParams.age)

        injector.removeInjectable(testViewModelWithParams::class)

        injector.reset()
    }
}
