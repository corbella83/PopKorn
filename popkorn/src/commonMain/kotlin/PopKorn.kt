package cc.popkorn

import cc.popkorn.core.config.InjectorConfig
import kotlin.reflect.KClass

/**
 * PopKorn DI
 *
 * @author Pau Corbella
 * @since 1.1.0
 */

private val injector by lazy { createDefaultInjector() }

/**
 * Method to obtain the default injector currently being used
 */
fun popKorn(): InjectorController = injector


/**
 * Methods to lazy inject instances anywhere like:
 *  val instance by popkorn<SomeClass>()
 *  val instance by injecting<SomeClass>()
 */
inline fun <reified T : Any> popkorn(environment: String? = null, noinline config: (InjectorConfig.Builder.() -> Unit)? = null) =
    lazy { popKorn().inject(T::class, environment, config) }

inline fun <reified T : Any> injecting(environment: String? = null, noinline config: (InjectorConfig.Builder.() -> Unit)? = null) =
    lazy { popKorn().inject(T::class, environment, config) }


/**
 * Methods to inject instances anywhere like inject<SomeClass>()
 */
inline fun <reified T : Any> inject(environment: String? = null, noinline config: (InjectorConfig.Builder.() -> Unit)? = null) =
    popKorn().inject(T::class, environment, config)

fun <T : Any> KClass<T>.inject(environment: String? = null, config: (InjectorConfig.Builder.() -> Unit)? = null) =
    injector.inject(this, environment, config)


/**
 * Methods to inject nullable instances anywhere like injectOrNull<SomeClass>()
 */
inline fun <reified T : Any> injectOrNull(environment: String? = null, noinline config: (InjectorConfig.Builder.() -> Unit)? = null) =
    popKorn().injectOrNull(T::class, environment, config)

fun <T : Any> KClass<T>.injectOrNull(environment: String? = null, config: (InjectorConfig.Builder.() -> Unit)? = null) =
    injector.injectOrNull(this, environment, config)
