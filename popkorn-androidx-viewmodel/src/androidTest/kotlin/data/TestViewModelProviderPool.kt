package cc.popkorn.androidx.viewModel.data

import cc.popkorn.core.exceptions.ProviderNotFoundException
import cc.popkorn.pools.ProviderPool
import cc.popkorn.providers.Provider
import kotlin.reflect.KClass

class TestViewModelProviderPool : ProviderPool {

    override fun <T : Any> isPresent(clazz: KClass<T>): Boolean {
        return when (clazz) {
            TestViewModelWithParams::class -> true
            else -> false
        }
    }

    override fun <T : Any> create(clazz: KClass<T>): Provider<T> {
        return when (clazz) {
            TestViewModelWithParams::class -> TestViewModelWithParamsProvider() as Provider<T>
            else -> throw ProviderNotFoundException(clazz)
        }
    }
}
