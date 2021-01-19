package cc.popkorn.data

import cc.popkorn.core.exceptions.ProviderNotFoundException
import cc.popkorn.pools.ProviderPool
import cc.popkorn.providers.Provider
import kotlin.reflect.KClass

/**
 * ResolverPool that define how test classes are created
 *
 * @author Pau Corbella
 * @since 2.0.0
 */
class TestProviderPool : ProviderPool {

    override fun <T : Any> isPresent(clazz: KClass<T>): Boolean {
        return when (clazz) {
            TestClassByApp::class -> true
            TestClassByUse::class -> true
            TestClassByHolder::class -> true
            TestClassByNew::class -> true
            TestClassByNewAssisted::class -> true
            TestClassByNewAssisted2::class -> true
            TestCascadeClass::class -> true
            else -> false
        }
    }

    override fun <T : Any> create(clazz: KClass<T>): Provider<T> {
        return when (clazz) {
            TestClassByApp::class -> TestClassByAppProvider() as Provider<T>
            TestClassByUse::class -> TestClassByUseProvider() as Provider<T>
            TestClassByHolder::class -> TestClassByHolderProvider() as Provider<T>
            TestClassByNew::class -> TestClassByNewProvider() as Provider<T>
            TestClassByNewAssisted::class -> TestClassByNewAssistedProvider() as Provider<T>
            TestClassByNewAssisted2::class -> TestClassByNewAssisted2Provider() as Provider<T>
            TestCascadeClass::class -> TestCascadeClassProvider() as Provider<T>
            else -> throw ProviderNotFoundException(clazz)
        }
    }
}
