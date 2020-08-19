package cc.popkorn


/**
 * Wrapper for Injector to be used from the JVM (pure java)
 *
 * @author Pau Corbella
 * @since 1.6.0
 */
class InjectorJVM(private val injector: InjectorController) {

    fun <T : Any> addInjectable(instance: T, type: Class<out T>, environment: String) = injector.addInjectable(instance, type.kotlinClass(), environment)

    fun <T : Any> addInjectable(instance: T, type: Class<out T>) = injector.addInjectable(instance, type.kotlinClass())

    fun <T : Any> addInjectable(instance: T, environment: String) = injector.addInjectable(instance, environment = environment)

    fun <T : Any> addInjectable(instance: T) = injector.addInjectable(instance)


    fun <T : Any> removeInjectable(type: Class<T>, environment: String) = injector.removeInjectable(type.kotlinClass(), environment)

    fun <T : Any> removeInjectable(type: Class<T>) = injector.removeInjectable(type.kotlinClass())


    fun <T : Any> inject(clazz: Class<T>, environment: String) = injector.inject(clazz.kotlinClass(), environment)

    fun <T : Any> inject(clazz: Class<T>) = injector.inject(clazz.kotlinClass(), null)

    fun <T : Any> injectNullable(clazz: Class<T>, environment: String) = injector.injectNullable(clazz.kotlinClass(), environment)

    fun <T : Any> injectNullable(clazz: Class<T>) = injector.injectNullable(clazz.kotlinClass(), null)


    fun reset() = injector.reset()

    fun purge() = injector.purge()

    //If it doesn't exist, creates a ClassReference
    private fun <T : Any> Class<T>.kotlinClass() = kotlin

}