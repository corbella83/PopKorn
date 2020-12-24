package cc.popkorn

import cc.popkorn.core.model.Environment
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


    // Provide raw objects or an InstanceJS<*> object for more information
    // If want to specify the environment, do it with Environment("value")
    fun <T : Any> create(clazz: JsClass<T>, vararg assistedInstances: Any): T {
        val environment = assistedInstances.singleOrNull { it is Environment } as? Environment
        val parameters = assistedInstances.filterNot { it is Environment }.map { if (it is InstanceJS<*>) it.toKotlin() else it }
        return injector.create(clazz.kotlinClass(), parameters, environment?.value)
    }

    fun reset() = injector.reset()

    fun purge() = injector.purge()


    private fun <T : Any> InstanceJS<T>.toKotlin() = Instance(instance, type.kotlinClass(), environment)

    //If it doesn't exist, creates a SimpleKClassImpl
    private fun <T : Any> JsClass<T>.kotlinClass() = kotlin
}