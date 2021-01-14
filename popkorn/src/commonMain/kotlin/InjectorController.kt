package cc.popkorn

import cc.popkorn.core.builder.CreatorBuilder
import kotlin.reflect.KClass

/**
 * Interface with the available methods to manage injections
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
interface InjectorController : InjectorManager {

    fun <T : Any> addInjectable(instance: T, type: KClass<out T>, environment: String? = null)

    fun <T : Any> addInjectable(instance: T, environment: String? = null) = addInjectable(instance, instance::class, environment)

    fun <T : Any> removeInjectable(type: KClass<T>, environment: String? = null)

    fun <T : Any> create(clazz: KClass<T>, environment: String? = null): T

    fun <T : Any> willCreate(clazz: KClass<T>, environment: String? = null): CreatorBuilder<T>

    fun purge()

    fun reset()

}
