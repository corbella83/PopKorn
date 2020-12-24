package cc.popkorn

import cc.popkorn.core.model.Environment
import kotlin.reflect.KClass


/**
 * PopKorn DI
 *
 * @author Pau Corbella
 * @since 1.1.0
 */

internal val injector by lazy { createDefaultInjector() }

/**
 * Methods to use PopKorn
 * popKorn() -> Call this to obtain the injector currently being used
 * val instance by popkorn<SomeClass>() -> Call this to lazy inject dependencies
 * val instance by assist<SomeClass>(assisted, params) -> Call this to lazy create dependencies
 */
fun popKorn(): InjectorController = injector

inline fun <reified T : Any> popkorn(environment: String? = null, vararg assistedInstances: Any): Lazy<T> = LazyDelegate { T::class.inject(environment) }

inline fun <reified T : Any> lazyInject(environment: String? = null): Lazy<T> = LazyDelegate { T::class.inject(environment) }

inline fun <reified T : Any> lazyCreate(vararg assistedInstances: Any): Lazy<T> = LazyDelegate { T::class.create(*assistedInstances) }


/**
 * Methods to inject instances anywhere like inject<SomeClass>()
 */
inline fun <reified T : Any> inject(environment: String? = null) = T::class.inject(environment)

fun <T : Any> KClass<T>.inject(environment: String? = null) = injector.inject(this, environment)


/**
 * Methods to inject nullable instances anywhere like injectNullable<SomeClass>()
 */
inline fun <reified T : Any> injectNullable(environment: String? = null) = T::class.injectNullable(environment)

fun <T : Any> KClass<T>.injectNullable(environment: String? = null) = injector.injectNullable(this, environment)


/**
 * Methods to create instances anywhere like create<SomeClass>(assisted, params)
 */
inline fun <reified T : Any> create(vararg assistedInstances: Any) = T::class.create(*assistedInstances)

fun <T : Any> KClass<T>.create(vararg assistedInstances: Any): T {
    val environment = assistedInstances.singleOrNull { it is Environment } as? Environment
    val parameters = assistedInstances.filterNot { it is Environment }
    return injector.create(this, parameters, environment?.value)
}

