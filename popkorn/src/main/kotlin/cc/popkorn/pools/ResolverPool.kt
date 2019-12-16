@file:Suppress("UNCHECKED_CAST")
package cc.popkorn.pools

import cc.popkorn.core.Resolver
import kotlin.reflect.KClass

/**
 * Implementation to get the provider via reflection
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
interface ResolverPool {

    fun <T : Any> isPresent(clazz: KClass<T>) : Boolean

    fun <T : Any> create(clazz: KClass<T>): Resolver<T>

}