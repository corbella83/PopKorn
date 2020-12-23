package cc.popkorn

import cc.popkorn.core.AssistedInjector
import cc.popkorn.core.model.Instance


/**
 * Wrapper for Injector to be used from the JS (pure js)
 *
 * @author Pau Corbella
 * @since 2.0.0
 */
class InjectorJS(private val injector: InjectorController) {

    class InstanceJS<T : Any>(val instance: T, val type: JsClass<out T>, val environment: String? = null)


    fun <T : Any> addInjectable(instance: T, type: JsClass<out T>, environment: String) = injector.addInjectable(instance, type.kotlinClass(), environment)

    fun <T : Any> addInjectable(instance: T, type: JsClass<out T>) = injector.addInjectable(instance, type.kotlinClass())

    fun <T : Any> addInjectable(instance: T, environment: String) = injector.addInjectable(instance, environment = environment)

    fun <T : Any> addInjectable(instance: T) = injector.addInjectable(instance)


    fun <T : Any> removeInjectable(type: JsClass<T>, environment: String) = injector.removeInjectable(type.kotlinClass(), environment)

    fun <T : Any> removeInjectable(type: JsClass<T>) = injector.removeInjectable(type.kotlinClass())


    fun <T : Any> inject(clazz: JsClass<T>, environment: String) = injector.inject(clazz.kotlinClass(), environment)

    fun <T : Any> inject(clazz: JsClass<T>) = injector.inject(clazz.kotlinClass(), null)

    fun <T : Any> injectNullable(clazz: JsClass<T>, environment: String) = injector.injectNullable(clazz.kotlinClass(), environment)

    fun <T : Any> injectNullable(clazz: JsClass<T>) = injector.injectNullable(clazz.kotlinClass(), null)


    fun <T : Any> create(clazz: JsClass<T>, environment: String, vararg providedInstances: InstanceJS<*>) =
        injector.createInstance(clazz.kotlinClass(), environment, *providedInstances.map { it.toKotlin() }.toTypedArray())

    fun <T : Any> create(clazz: JsClass<T>, vararg providedInstances: InstanceJS<*>) =
        injector.createInstance(clazz.kotlinClass(), null, *providedInstances.map { it.toKotlin() }.toTypedArray())


    fun reset() = injector.reset()

    fun purge() = injector.purge()


    private fun <T : Any> InstanceJS<T>.toKotlin() = Instance(instance, type.kotlinClass(), environment)

    //If it doesn't exist, creates a SimpleKClassImpl
    private fun <T : Any> JsClass<T>.kotlinClass() = kotlin
}