package cc.popkorn

import cc.popkorn.core.model.Instance
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


inline fun <reified T : Any> InjectorController.inject(environment: String? = null) = this.inject(T::class, environment)

inline fun <reified T : Any> InjectorController.injectNullable(environment: String? = null) = this.injectNullable(T::class, environment)

inline fun <reified T : Any> InjectorController.create(vararg params: Any): T {
    val list = params.map { if (it is Instance<*>) it else Instance(it) }
    return this.createInstance(T::class, null, *list.toTypedArray())
}

inline fun <reified T : Any> InjectorController.createWithEnvironment(environment: String, vararg params: Any): T {
    val list = params.map { if (it is Instance<*>) it else Instance(it) }
    return this.createInstance(T::class, environment, *list.toTypedArray())
}

// if want no holder -> val tmp = Unit.inject() or popKorn().inject()
//inline fun <reified T : Any> Any.inject(environment: String? = null) = T::class.inject(this, environment)
//
//fun <T : Any> KClass<T>.inject(holder:Any, environment: String? = null) = injector.inject(this, holder, environment)
