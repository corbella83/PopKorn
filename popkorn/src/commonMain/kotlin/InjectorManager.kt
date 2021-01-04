package cc.popkorn

import kotlin.reflect.KClass

/**
 * Interface with the available methods to manage injections
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
interface InjectorManager {

    fun <T : Any> inject(clazz: KClass<T>, environment: String? = null): T

    fun <T : Any> injectNullable(clazz: KClass<T>, environment: String? = null): T?

}