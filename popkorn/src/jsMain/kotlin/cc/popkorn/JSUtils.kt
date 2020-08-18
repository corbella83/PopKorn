package cc.popkorn

import cc.popkorn.core.Injector
import cc.popkorn.mapping.Mapping
import cc.popkorn.pools.*
import org.w3c.dom.Document
import kotlin.reflect.KClass


/**
 * JS-specific utilities class
 *
 * @author Pau Corbella
 * @since 1.6.0
 */


internal fun jsResolverPool(): ResolverPool {
    return loadMappings(RESOLVER_MAPPINGS)
        .takeIf { it.isNotEmpty() }
        ?.let { MappingResolverPool(it) }
        ?: throw RuntimeException("Could not load Resolver Mappings")
}

internal fun jsProviderPool(): ProviderPool {
    return loadMappings(PROVIDER_MAPPINGS)
        .takeIf { it.isNotEmpty() }
        ?.let { MappingProviderPool(it) }
        ?: throw RuntimeException("Could not load Provider Mappings")
}


private fun loadMappings(type: String): Set<Mapping> {
    //TODO find all mappings
    return hashSetOf()
}

