package cc.popkorn


/**
 * Wrapper for Injector to be used from the JS (pure js)
 *
 * @author Pau Corbella
 * @since 1.6.0
 */
class InjectorJS(private val injector: InjectorController) {

    fun <T : Any> addInjectable(instance: T, type: JsClass<out T>, environment: String) = injector.addInjectable(instance, type.kotlin, environment)

    fun <T : Any> addInjectable(instance: T, type: JsClass<out T>) = injector.addInjectable(instance, type.kotlin)

    fun <T : Any> addInjectable(instance: T, environment: String) = injector.addInjectable(instance, environment = environment)

    fun <T : Any> addInjectable(instance: T) = injector.addInjectable(instance)


    fun <T : Any> removeInjectable(type: JsClass<T>, environment: String) = injector.removeInjectable(type.kotlin, environment)

    fun <T : Any> removeInjectable(type: JsClass<T>) = injector.removeInjectable(type.kotlin)


    fun <T : Any> inject(clazz: JsClass<T>, environment: String) = injector.inject(clazz.kotlin, environment)

    fun <T : Any> inject(clazz: JsClass<T>) = injector.inject(clazz.kotlin, null)

    fun <T : Any> injectNullable(clazz: JsClass<T>, environment: String) = injector.injectNullable(clazz.kotlin, environment)

    fun <T : Any> injectNullable(clazz: JsClass<T>) = injector.injectNullable(clazz.kotlin, null)


    fun reset() = injector.reset()

    fun purge() = injector.purge()

}