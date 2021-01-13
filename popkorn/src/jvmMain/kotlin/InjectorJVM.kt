package cc.popkorn

import cc.popkorn.core.model.Environment
import cc.popkorn.core.model.Instance

/**
 * Wrapper for Injector to be used from the JVM (pure java)
 *
 * @author Pau Corbella
 * @since 2.0.0
 */
class InjectorJVM(private val injector: InjectorController) {

    class InstanceJVM<T : Any>(val instance: T, val type: Class<out T> = instance::class.java, val environment: String? = null)

    fun <T : Any> addInjectable(instance: T, type: Class<out T>, environment: String) = injector.addInjectable(instance, type.kotlinClass(), environment)

    fun <T : Any> addInjectable(instance: T, type: Class<out T>) = injector.addInjectable(instance, type.kotlinClass())

    fun <T : Any> addInjectable(instance: T, environment: String) = injector.addInjectable(instance, environment = environment)

    fun <T : Any> addInjectable(instance: T) = injector.addInjectable(instance)


    fun <T : Any> removeInjectable(type: Class<T>, environment: String) = injector.removeInjectable(type.kotlinClass(), environment)

    fun <T : Any> removeInjectable(type: Class<T>) = injector.removeInjectable(type.kotlinClass())


    fun <T : Any> inject(clazz: Class<T>, environment: String) = injector.inject(clazz.kotlinClass(), environment)

    fun <T : Any> inject(clazz: Class<T>) = injector.inject(clazz.kotlinClass(), null)

    fun <T : Any> injectOrNull(clazz: Class<T>, environment: String) = injector.injectOrNull(clazz.kotlinClass(), environment)

    fun <T : Any> injectOrNull(clazz: Class<T>) = injector.injectOrNull(clazz.kotlinClass(), null)


    // Provide raw objects or an InstanceJVM<*> object for more information
    // If want to specify the environment, do it with Environment("value")
    fun <T : Any> create(clazz: Class<T>, vararg assistedInstances: Any): T {
        val environment = assistedInstances.singleOrNull { it is Environment } as? Environment
        val factory = assistedInstances.filterNot { it is Environment }.toFactory()
        return injector.create(clazz.kotlinClass(), environment?.value, factory)
    }


    fun reset() = injector.reset()

    fun purge() = injector.purge()


    private fun List<Any>.toFactory(): ParametersFactory {
        return ParametersFactory.Builder().apply {
            forEach { add(if (it is InstanceJVM<*>) it.toKotlin() else Instance(it)) }
        }.build()
    }

    private fun <T : Any> InstanceJVM<T>.toKotlin() = Instance(instance, type.kotlinClass(), environment)

    // If it doesn't exist, creates a ClassReference
    private fun <T : Any> Class<T>.kotlinClass() = kotlin

}
