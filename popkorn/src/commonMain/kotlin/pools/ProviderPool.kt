@file:Suppress("UNCHECKED_CAST")

package cc.popkorn.pools

import cc.popkorn.providers.Provider
import kotlin.reflect.KClass

/**
 * Interface to define how to obtain the providers
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
interface ProviderPool {

    fun <T : Any> isPresent(clazz: KClass<T>): Boolean

    fun <T : Any> create(clazz: KClass<T>): Provider<T>

}