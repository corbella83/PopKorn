package cc.popkorn

import cc.popkorn.mapping.Mapping


/**
 * Compatibility class to use PopKorn from js code
 *
 * @author Pau Corbella
 * @since 2.0.0
 */


internal lateinit var iResolverMappings: Set<Mapping>
internal lateinit var iProviderMappings: Set<Mapping>

/**
 * This method needs to be called on JS platform before using PopKorn. This is because in js cannot
 * automatically locate the mapping files
 *
 * @param resolverMappings All the resolver mappings that PopKorn generated
 * @param providerMappings All the provider mappings that PopKorn generated
 */
fun setup(resolverMappings: Set<Mapping>, providerMappings: Set<Mapping>) {
    if (::iResolverMappings.isInitialized || ::iProviderMappings.isInitialized) return
    iResolverMappings = resolverMappings
    iProviderMappings = providerMappings
}

