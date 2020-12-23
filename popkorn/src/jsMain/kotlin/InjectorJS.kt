package cc.popkorn


/**
 * Wrapper for Injector to be used from the JS (pure js)
 *
 * @author Pau Corbella
 * @since 2.0.0
 */
class InjectorJS(private val injector: InjectorController) {

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


    fun reset() = injector.reset()

    fun purge() = injector.purge()

    //If it doesn't exist, creates a SimpleKClassImpl
    private fun <T : Any> JsClass<T>.kotlinClass() = kotlin
}