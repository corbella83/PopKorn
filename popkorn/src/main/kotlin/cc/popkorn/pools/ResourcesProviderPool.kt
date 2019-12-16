@file:Suppress("UNCHECKED_CAST")
package cc.popkorn.pools

import cc.popkorn.core.Provider
import cc.popkorn.mapping.Mapping
import kotlin.reflect.KClass

/**
 * Implementation to get providers via resource mappings
 *
 * @author Pau Corbella
 * @since 1.3.0
 */
internal class ResourcesProviderPool(private val mappings:Set<Mapping>) : ProviderPool {

    override fun <T : Any> isPresent(clazz: KClass<T>) =  findProvider(clazz)!=null

    override fun <T : Any> create(clazz: KClass<T>): Provider<T> {
        return findProvider(clazz)
            ?.let { it as? Provider<T> }
            ?: throw RuntimeException("Could not find Provider for this class: ${clazz.qualifiedName}. Did you forget to add @Injectable?")
    }

    private fun findProvider(original:KClass<*>) : Any?{
        mappings.forEach { map ->
            map.find(original)?.also { return it }
        }
        return null
    }

}