package cc.popkorn

import cc.popkorn.core.Injector


class InjectorJVM(private val injector: Injector) {

    fun <T : Any> addInjectable(instance: T, type: Class<out T>, environment: String) = injector.addInjectable(instance, type.kotlin, environment)

    fun <T : Any> addInjectable(instance: T, type: Class<out T>) = injector.addInjectable(instance, type.kotlin)

    fun <T : Any> addInjectable(instance: T, environment: String) = injector.addInjectable(instance, environment = environment)

    fun <T : Any> addInjectable(instance: T) = injector.addInjectable(instance)


    fun <T : Any> removeInjectable(type: Class<T>, environment: String) = injector.removeInjectable(type.kotlin, environment)

    fun <T : Any> removeInjectable(type: Class<T>) = injector.removeInjectable(type.kotlin)


    fun <T : Any> inject(clazz: Class<T>, environment: String) = injector.inject(clazz.kotlin, environment)

    fun <T : Any> inject(clazz: Class<T>) = injector.inject(clazz.kotlin, null)

    fun <T : Any> injectNullable(clazz: Class<T>, environment: String) = injector.injectNullable(clazz.kotlin, environment)

    fun <T : Any> injectNullable(clazz: Class<T>) = injector.injectNullable(clazz.kotlin, null)


    fun reset() = injector.reset()

    fun purge() = injector.purge()

}