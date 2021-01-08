package cc.popkorn.core.model

import kotlin.reflect.KClass

/**
 * Model class to be used to identify an instance
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
data class Instance<T : Any>(
    val instance: T,
    val type: KClass<out T> = instance::class,
    val environment: String? = null
)
