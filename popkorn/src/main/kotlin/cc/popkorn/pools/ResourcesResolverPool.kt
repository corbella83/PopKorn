@file:Suppress("UNCHECKED_CAST")
package cc.popkorn.pools

import cc.popkorn.core.Resolver
import cc.popkorn.exceptions.ResolverNotFoundException
import cc.popkorn.mapping.Mapping
import kotlin.reflect.KClass

/**
 * Implementation to get resolvers via resource mappings
 *
 * @author Pau Corbella
 * @since 1.3.0
 */
internal class ResourcesResolverPool(private val mappings:Set<Mapping>) : ResolverPool {

    override fun <T : Any> isPresent(clazz: KClass<T>) =  findResolver(clazz)!=null

    override fun <T : Any> create(clazz: KClass<T>): Resolver<T> {
        return findResolver(clazz)
            ?.let { it as? Resolver<T> }
            ?: throw ResolverNotFoundException(clazz)
    }

    private fun findResolver(original:KClass<*>) : Any?{
        mappings.forEach { map ->
            map.find(original)?.also { return it }
        }
        return null
    }

}