@file:Suppress("UNCHECKED_CAST")

package cc.popkorn.pools

import cc.popkorn.resolvers.Resolver
import kotlin.reflect.KClass

/**
 * Interface to define how to obtain the resolvers
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
interface ResolverPool {

    fun <T : Any> isPresent(clazz: KClass<T>): Boolean

    fun <T : Any> create(clazz: KClass<T>): Resolver<T>
}
