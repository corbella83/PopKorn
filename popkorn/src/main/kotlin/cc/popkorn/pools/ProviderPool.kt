@file:Suppress("UNCHECKED_CAST")
package cc.popkorn.pools

import cc.popkorn.core.Provider
import mapping.Mapping
import kotlin.reflect.KClass

/**
 * Implementation to get the provider via reflection
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class ProviderPool(private val mappings:LinkedHashSet<Mapping> = linkedSetOf()) {

    fun <T : Any> isPresent(clazz: KClass<T>) =  mappings.any{ it.isPresent(clazz) }

    fun addMapping(mapping: Mapping) = mappings.add(mapping)

    fun <T : Any> create(clazz: KClass<T>): Provider<T> {
        return findProvider(clazz)
            ?.newInstance()
            ?.let { it as? Provider<T> }
            ?: throw RuntimeException("Could not find Provider for this class: ${clazz.qualifiedName}. Did you forget to add @Injectable?")
    }


    private fun findProvider(original:KClass<*>) : Class<*>?{
        mappings.forEach { map ->
            map.find(original)?.also { return it.java }
        }
        return null
    }

}