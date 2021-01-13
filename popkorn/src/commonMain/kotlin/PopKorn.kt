package cc.popkorn

import cc.popkorn.core.model.Environment
import cc.popkorn.core.model.Instance
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
 * val instance by injecting<SomeClass>() -> Call this to lazy inject dependencies
 * val instance by creating<SomeClass>(param1, param2) -> Call this to lazy create dependencies
 */
fun popKorn(): InjectorController = injector

inline fun <reified T : Any> popkorn(environment: String? = null) = lazy { T::class.inject(environment) }

inline fun <reified T : Any> injecting(environment: String? = null) = lazy { T::class.inject(environment) }

// TODO
inline fun <reified T : Any> creating(vararg assistedInstances: Any) = lazy {
    val environment = assistedInstances.singleOrNull { it is Environment } as? Environment
    T::class.create(environment?.value) {
        assistedInstances.filterNot { it is Environment }
            .forEach { add(if (it is Instance<*>) it else Instance(it)) }
    }
}


/**
 * Methods to inject instances anywhere like inject<SomeClass>()
 */
inline fun <reified T : Any> inject(environment: String? = null) = T::class.inject(environment)

fun <T : Any> KClass<T>.inject(environment: String? = null) = injector.inject(this, environment)


/**
 * Methods to inject nullable instances anywhere like injectOrNull<SomeClass>()
 */
inline fun <reified T : Any> injectOrNull(environment: String? = null) = T::class.injectOrNull(environment)

fun <T : Any> KClass<T>.injectOrNull(environment: String? = null) = injector.injectOrNull(this, environment)


/**
 * Methods to create instances anywhere like create<SomeClass>{ add("param1"); add("param2"); }
 */
inline fun <reified T : Any> create(environment: String? = null, noinline parameters: (ParametersFactory.Builder.() -> Unit)? = null) = T::class.create(environment, parameters)

fun <T : Any> KClass<T>.create(environment: String? = null, parameters: (ParametersFactory.Builder.() -> Unit)? = null): T {
    val params = parameters?.let { ParametersFactory.Builder().also(it).build() }
    return injector.create(this, environment, params)
}
