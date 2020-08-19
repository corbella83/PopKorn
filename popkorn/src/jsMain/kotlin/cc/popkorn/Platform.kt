package cc.popkorn

import cc.popkorn.core.Injector
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
    return loadMappings(RESOLVER_MAPPINGS)
        .takeIf { it.isNotEmpty() }
        ?.let { MappingResolverPool(it) }
        ?: throw RuntimeException("Could not load Resolver Mappings")
}

private fun jsProviderPool(): ProviderPool {
    return loadMappings(PROVIDER_MAPPINGS)
        .takeIf { it.isNotEmpty() }
        ?.let { MappingProviderPool(it) }
        ?: throw RuntimeException("Could not load Provider Mappings")
}


private fun loadMappings(type: String): Set<Mapping> {
    //TODO find all mappings
    return hashSetOf()
}

