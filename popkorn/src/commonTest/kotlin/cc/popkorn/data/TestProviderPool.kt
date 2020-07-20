package cc.popkorn.data

import cc.popkorn.pools.ProviderPool
import cc.popkorn.providers.Provider
import kotlin.reflect.KClass

class TestProviderPool : ProviderPool {

    override fun <T : Any> isPresent(clazz: KClass<T>): Boolean {
        return when (clazz) {
            TestClassByApp::class -> true
            TestClassByNew::class -> true
            TestClassByUse::class -> true
            else -> false
        }
    }

    override fun <T : Any> create(clazz: KClass<T>): Provider<T> {
        return when (clazz) {
            TestClassByApp::class -> TestClassByApp_Provider() as Provider<T>
            TestClassByNew::class -> TestClassByNew_Provider() as Provider<T>
            TestClassByUse::class -> TestClassByUse_Provider() as Provider<T>
            else -> throw RuntimeException("Provider not found : $clazz")
        }
    }
}