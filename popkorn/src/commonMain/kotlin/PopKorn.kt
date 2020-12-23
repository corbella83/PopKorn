package cc.popkorn

import kotlin.reflect.KClass


/**
 * PopKorn DI
 *
 * @author Pau Corbella
 * @since 1.1.0
 */

internal val injector by lazy { createDefaultInjector() }

fun popKorn(): InjectorController = injector

@Deprecated("Use Unit.inject() instead")
inline fun <reified T : Any> inject(environment: String? = null) = T::class.inject(environment)

fun <T : Any> KClass<T>.inject(environment: String? = null) = injector.inject(this, environment)


// if want no holder -> val tmp = Unit.inject() or popKorn().inject()
//inline fun <reified T : Any> Any.inject(environment: String? = null) = T::class.inject(this, environment)
//
//fun <T : Any> KClass<T>.inject(holder:Any, environment: String? = null) = injector.inject(this, holder, environment)
