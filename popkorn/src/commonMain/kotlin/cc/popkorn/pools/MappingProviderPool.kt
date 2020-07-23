@file:Suppress("UNCHECKED_CAST")

package cc.popkorn.pools

import cc.popkorn.core.exceptions.ProviderNotFoundException
import cc.popkorn.mapping.Mapping
import cc.popkorn.providers.Provider
import kotlin.reflect.KClass

/**
 * Implementation to get providers via mappings
 *
 * @author Pau Corbella
 * @since 1.3.0
 */
class MappingProviderPool(private val mappings: Set<Mapping>) :
    ProviderPool {

    override fun <T : Any> isPresent(clazz: KClass<T>) = findProvider(clazz) != null

    override fun <T : Any> create(clazz: KClass<T>): Provider<T> {
        return findProvider(clazz)
            ?.let { it as? Provider<T> }
            ?: throw ProviderNotFoundException(clazz)
    }

    private fun findProvider(original: KClass<*>): Any? {
        mappings.forEach { map ->
            map.find(original)?.also { return it }
        }
        return null
    }

}