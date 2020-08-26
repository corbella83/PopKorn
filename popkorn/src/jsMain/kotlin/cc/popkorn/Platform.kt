package cc.popkorn

import cc.popkorn.core.Injector
import cc.popkorn.core.exceptions.PopKornNotInitializedException
import cc.popkorn.mapping.Mapping
import cc.popkorn.pools.MappingProviderPool
import cc.popkorn.pools.MappingResolverPool
import cc.popkorn.pools.ProviderPool
import cc.popkorn.pools.ResolverPool
import kotlin.reflect.KClass


/**
 * Implementation for JS of the methods/classes that are Platform-dependent
 *
 * @author Pau Corbella
 * @since 1.6.0
 */


actual class WeakReference<T : Any> actual constructor(referred: T) {
    private var pointer: T? = referred

    actual fun clear() {
        pointer = null
    }

    actual fun get() = pointer

}

internal actual fun <T : Any> KClass<T>.getName() = js.name

internal actual fun <T : Any> KClass<T>.needsResolver(resolverPool: ResolverPool) = resolverPool.isPresent(this)

internal actual fun createDefaultInjector() = Injector(jsResolverPool(), jsProviderPool())


private fun jsResolverPool(): ResolverPool {
    if (!::iResolverMappings.isInitialized || !::iProviderMappings.isInitialized) throw PopKornNotInitializedException()
    return MappingResolverPool(iProviderMappings)
}

private fun jsProviderPool(): ProviderPool {
    if (!::iResolverMappings.isInitialized || !::iProviderMappings.isInitialized) throw PopKornNotInitializedException()
    return MappingProviderPool(iProviderMappings)
}
