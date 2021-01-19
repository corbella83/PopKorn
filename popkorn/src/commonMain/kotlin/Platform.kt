package cc.popkorn

import cc.popkorn.core.Injector
import cc.popkorn.pools.ResolverPool
import kotlin.reflect.KClass

/**
 * Methods/Classes used in library that are Platform-dependent
 *
 * @author Pau Corbella
 * @since 2.0.0
 */

expect class WeakReference<T : Any>(referred: T) {
    fun clear()
    fun get(): T?
}

internal expect fun <T : Any> KClass<T>.getName(): String

internal expect fun <T : Any> KClass<T>.needsResolver(resolverPool: ResolverPool): Boolean

internal expect fun createDefaultInjector(): Injector
