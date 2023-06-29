package cc.popkorn

import cc.popkorn.config.CreatorConfigBuilder
import cc.popkorn.config.InjectorConfigBuilder

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

    fun <T : Any> inject(clazz: JsClass<T>) = injector.inject(clazz.kotlinClass())
    fun <T : Any> inject(clazz: JsClass<T>, environment: String) = injector.inject(clazz.kotlinClass(), environment)
    fun <T : Any> inject(clazz: JsClass<T>, config: InjectorConfigBuilder) = injector.inject(clazz.kotlinClass()) { config.apply(this) }
    fun <T : Any> inject(clazz: JsClass<T>, environment: String, config: InjectorConfigBuilder) = injector.inject(clazz.kotlinClass(), environment) { config.apply(this) }

    fun <T : Any> injectOrNull(clazz: JsClass<T>) = injector.injectOrNull(clazz.kotlinClass())
    fun <T : Any> injectOrNull(clazz: JsClass<T>, environment: String) = injector.injectOrNull(clazz.kotlinClass(), environment)
    fun <T : Any> injectOrNull(clazz: JsClass<T>, config: InjectorConfigBuilder) = injector.injectOrNull(clazz.kotlinClass()) { config.apply(this) }
    fun <T : Any> injectOrNull(clazz: JsClass<T>, environment: String, config: InjectorConfigBuilder) = injector.injectOrNull(clazz.kotlinClass(), environment) { config.apply(this) }

    fun <T : Any> create(clazz: JsClass<T>) = injector.create(clazz.kotlinClass())
    fun <T : Any> create(clazz: JsClass<T>, environment: String) = injector.create(clazz.kotlinClass(), environment)
    fun <T : Any> create(clazz: JsClass<T>, config: CreatorConfigBuilder) = injector.create(clazz.kotlinClass()) { config.apply(this) }
    fun <T : Any> create(clazz: JsClass<T>, environment: String, config: CreatorConfigBuilder) = injector.create(clazz.kotlinClass(), environment) { config.apply(this) }

    fun reset() = injector.reset()
    fun purge() = injector.purge()
}
