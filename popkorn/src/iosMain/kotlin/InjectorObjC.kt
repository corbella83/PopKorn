package cc.popkorn

import cc.popkorn.config.CreatorConfigBuilder
import cc.popkorn.config.InjectorConfigBuilder
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.ObjCProtocol

/**
 * Wrapper for Injector to be used from the ObjectiveC (pure objc)
 *
 * @author Pau Corbella
 * @since 2.0.0
 */
class InjectorObjC(private val injector: InjectorController) {

    fun addInjectable(instance: Any, clazz: ObjCClass, environment: String) = injector.addInjectable(instance, clazz.kotlinClass(), environment)

    fun addInjectable(instance: Any, clazz: ObjCClass) = injector.addInjectable(instance, clazz.kotlinClass())

    fun addInjectable(instance: Any, protocol: ObjCProtocol, environment: String) = injector.addInjectable(instance, protocol.kotlinClass(), environment)

    fun addInjectable(instance: Any, protocol: ObjCProtocol) = injector.addInjectable(instance, protocol.kotlinClass())


    fun removeInjectable(clazz: ObjCClass, environment: String) = injector.removeInjectable(clazz.kotlinClass(), environment)

    fun removeInjectable(clazz: ObjCClass) = injector.removeInjectable(clazz.kotlinClass())

    fun removeInjectable(protocol: ObjCProtocol, environment: String) = injector.removeInjectable(protocol.kotlinClass(), environment)

    fun removeInjectable(protocol: ObjCProtocol) = injector.removeInjectable(protocol.kotlinClass())


    fun inject(clazz: ObjCClass) = injector.inject(clazz.kotlinClass())

    fun inject(clazz: ObjCClass, environment: String) = injector.inject(clazz.kotlinClass(), environment)

    fun inject(clazz: ObjCClass, config: InjectorConfigBuilder) = injector.inject(clazz.kotlinClass()) { config.apply(this) }

    fun inject(clazz: ObjCClass, environment: String, config: InjectorConfigBuilder) = injector.inject(clazz.kotlinClass(), environment) { config.apply(this) }


    fun inject(protocol: ObjCProtocol) = injector.inject(protocol.kotlinClass())

    fun inject(protocol: ObjCProtocol, environment: String) = injector.inject(protocol.kotlinClass(), environment)

    fun inject(protocol: ObjCProtocol, config: InjectorConfigBuilder) = injector.inject(protocol.kotlinClass()) { config.apply(this) }

    fun inject(protocol: ObjCProtocol, environment: String, config: InjectorConfigBuilder) = injector.inject(protocol.kotlinClass(), environment) { config.apply(this) }


    fun injectOrNull(clazz: ObjCClass) = injector.injectOrNull(clazz.kotlinClass())

    fun injectOrNull(clazz: ObjCClass, environment: String) = injector.injectOrNull(clazz.kotlinClass(), environment)

    fun injectOrNull(clazz: ObjCClass, config: InjectorConfigBuilder) = injector.injectOrNull(clazz.kotlinClass()) { config.apply(this) }

    fun injectOrNull(clazz: ObjCClass, environment: String, config: InjectorConfigBuilder) = injector.injectOrNull(clazz.kotlinClass(), environment) { config.apply(this) }


    fun injectOrNull(protocol: ObjCProtocol) = injector.injectOrNull(protocol.kotlinClass())

    fun injectOrNull(protocol: ObjCProtocol, environment: String) = injector.injectOrNull(protocol.kotlinClass(), environment)

    fun injectOrNull(protocol: ObjCProtocol, config: InjectorConfigBuilder) = injector.injectOrNull(protocol.kotlinClass()) { config.apply(this) }

    fun injectOrNull(protocol: ObjCProtocol, environment: String, config: InjectorConfigBuilder) = injector.injectOrNull(protocol.kotlinClass(), environment) { config.apply(this) }


    fun create(clazz: ObjCClass) = injector.create(clazz.kotlinClass())

    fun create(clazz: ObjCClass, environment: String) = injector.create(clazz.kotlinClass(), environment)

    fun create(clazz: ObjCClass, config: CreatorConfigBuilder) = injector.create(clazz.kotlinClass()) { config.apply(this) }

    fun create(clazz: ObjCClass, environment: String, config: CreatorConfigBuilder) = injector.create(clazz.kotlinClass(), environment) { config.apply(this) }


    fun create(protocol: ObjCProtocol) = injector.create(protocol.kotlinClass())

    fun create(protocol: ObjCProtocol, environment: String) = injector.create(protocol.kotlinClass(), environment)

    fun create(protocol: ObjCProtocol, config: CreatorConfigBuilder) = injector.create(protocol.kotlinClass()) { config.apply(this) }

    fun create(protocol: ObjCProtocol, environment: String, config: CreatorConfigBuilder) = injector.create(protocol.kotlinClass(), environment) { config.apply(this) }


    fun reset() = injector.reset()

    fun purge() = injector.purge()

}
