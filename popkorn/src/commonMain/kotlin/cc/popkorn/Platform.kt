package cc.popkorn

import kotlin.reflect.KClass

/**
 * Methods/Classes used in library that are Platform-dependent
 *
 * @author Pau Corbella
 * @since 1.6.0
 */


expect class WeakReference<T : Any>(referred: T) {
    fun clear()
    fun get(): T?
}

internal expect fun <T : Any> KClass<T>.getName(): String

internal expect fun <T : Any> KClass<T>.needsResolver(): Boolean
