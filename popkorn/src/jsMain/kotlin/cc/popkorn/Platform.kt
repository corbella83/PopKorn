package cc.popkorn

import cc.popkorn.pools.ResolverPool
import kotlin.reflect.KClass


/**
 * Implementation for JS of the methods/classes that are Platform-dependent
 *
 * @author Pau Corbella
 * @since 1.6.0
 */


actual class WeakReference<T : Any> actual constructor(referred: T) {
    private var pointer: T? = referred

    actual fun clear() {
        pointer = null
    }

    actual fun get() = pointer

}

internal actual fun <T : Any> KClass<T>.getName() = js.name

internal actual fun <T : Any> KClass<T>.needsResolver(resolverPool: ResolverPool) = resolverPool.isPresent(this)
