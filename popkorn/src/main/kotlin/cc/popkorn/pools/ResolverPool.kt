@file:Suppress("UNCHECKED_CAST")
package cc.popkorn.pools

import cc.popkorn.core.Resolver
import cc.popkorn.mapping.Mapping
import kotlin.collections.LinkedHashSet
import kotlin.reflect.KClass

/**
 * Implementation to get implementation of an interface via reflection
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
internal class ResolverPool(private val mappings:LinkedHashSet<Mapping> = linkedSetOf()) {

    fun <T : Any> isPresent(clazz: KClass<T>) =  mappings.any{ it.isPresent(clazz) }

    fun addMapping(mapping: Mapping) = mappings.add(mapping)

    fun <T : Any> create(clazz: KClass<T>): Resolver<T> {
        return findResolver(clazz)
            ?.let { it as? Resolver<T> }
            ?: throw RuntimeException("Could not find Resolver for this class: ${clazz.qualifiedName}. Is this interface being used by an Injectable class?")
    }

    private fun findResolver(original:KClass<*>) : Any?{
        mappings.forEach { map ->
            map.find(original)?.also { return it }
        }
        return null
    }

}