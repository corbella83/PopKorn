package cc.popkorn

import cc.popkorn.mapping.Mapping


/**
 * Compatibility class to use PopKorn from native code
 *
 * @author Pau Corbella
 * @since 2.0.0
 */


internal lateinit var resolverMappings: Set<Mapping>
internal lateinit var providerMappings: Set<Mapping>

/**
 * This method needs to be called on Native platform before using PopKorn. This is because in native cannot
 * automatically locate the mapping files
 *
 * @param resolvers All the resolver mappings that PopKorn generated
 * @param providers All the provider mappings that PopKorn generated
 */
fun setup(resolvers: Set<Mapping>, providers: Set<Mapping>) {
    if (::resolverMappings.isInitialized || ::providerMappings.isInitialized) return
    resolverMappings = resolvers
    providerMappings = providers
}

