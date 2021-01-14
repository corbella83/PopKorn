package cc.popkorn

import cc.popkorn.core.builder.InjectorBuilder
import kotlin.reflect.KClass

/**
 * Interface with the available methods to manage injections
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
interface InjectorManager {

    fun <T : Any> inject(clazz: KClass<T>, environment: String? = null): T

    fun <T : Any> injectOrNull(clazz: KClass<T>, environment: String? = null): T?

    fun <T : Any> willInject(clazz: KClass<T>, environment: String? = null): InjectorBuilder<T>

}
