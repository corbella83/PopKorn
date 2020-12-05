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


inline fun <reified T : Any> inject(environment: String? = null) = T::class.inject(environment)

inline fun <reified T : Any> lazyInject(environment: String? = null) = lazy { T::class.inject(environment) }

fun <T : Any> KClass<T>.inject(environment: String? = null) = injector.inject(this, environment)

