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

fun popKorn(): InjectorController = injector

@Deprecated("Use Unit.inject() instead")
inline fun <reified T : Any> inject(environment: String? = null) = T::class.inject(environment)

fun <T : Any> KClass<T>.inject(environment: String? = null) = injector.inject(this, environment)


inline fun <reified T : Any> InjectorController.inject(environment: String? = null) = this.inject(T::class, environment)

inline fun <reified T : Any> InjectorController.injectNullable(environment: String? = null) = this.injectNullable(T::class, environment)

inline fun <reified T : Any> InjectorController.create(vararg assistedInstances: Any): T {
    val environment = assistedInstances.singleOrNull { it is Environment } as? Environment
    val parameters = assistedInstances.filterNot { it is Environment }
    return this.create(T::class, parameters, environment?.value)
}

// if want no holder -> val tmp = Unit.inject() or popKorn().inject()
//inline fun <reified T : Any> Any.inject(environment: String? = null) = T::class.inject(this, environment)
//
//fun <T : Any> KClass<T>.inject(holder:Any, environment: String? = null) = injector.inject(this, holder, environment)
