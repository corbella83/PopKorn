package cc.popkorn

import kotlin.reflect.KClass


expect class WeakReference<T : Any>(referred: T) {
    fun clear()
    fun get(): T?
}

internal expect fun <T : Any> KClass<T>.getName(): String

internal expect fun <T : Any> KClass<T>.needsResolver(): Boolean
