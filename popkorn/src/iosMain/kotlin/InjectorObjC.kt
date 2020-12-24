package cc.popkorn

import cc.popkorn.core.model.Environment
import cc.popkorn.core.model.Instance
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.ObjCProtocol
import kotlinx.cinterop.getOriginalKotlinClass

/**
 * Wrapper for Injector to be used from the ObjectiveC (pure objc)
 *
 * @author Pau Corbella
 * @since 2.0.0
 */
class InjectorObjC(private val injector: InjectorController) {

    sealed class InstanceObjC(val instance: Any, val environment: String?) {
        class InstanceObjCClass(instance: Any, val type: ObjCClass, environment: String? = null) : InstanceObjC(instance, environment)
        class InstanceObjCProtocol(instance: Any, val type: ObjCProtocol, environment: String? = null) : InstanceObjC(instance, environment)
    }


    fun addInjectable(instance: Any, clazz: ObjCClass, environment: String) = injector.addInjectable(instance, clazz.kotlinClass(), environment)

    fun addInjectable(instance: Any, clazz: ObjCClass) = injector.addInjectable(instance, clazz.kotlinClass())

    fun addInjectable(instance: Any, protocol: ObjCProtocol, environment: String) = injector.addInjectable(instance, protocol.kotlinClass(), environment)

    fun addInjectable(instance: Any, protocol: ObjCProtocol) = injector.addInjectable(instance, protocol.kotlinClass())


    fun removeInjectable(clazz: ObjCClass, environment: String) = injector.removeInjectable(clazz.kotlinClass(), environment)

    fun removeInjectable(clazz: ObjCClass) = injector.removeInjectable(clazz.kotlinClass())

    fun removeInjectable(protocol: ObjCProtocol, environment: String) = injector.removeInjectable(protocol.kotlinClass(), environment)

    fun removeInjectable(protocol: ObjCProtocol) = injector.removeInjectable(protocol.kotlinClass())


    fun inject(clazz: ObjCClass, environment: String) = injector.inject(clazz.kotlinClass(), environment)

    fun inject(clazz: ObjCClass) = injector.inject(clazz.kotlinClass(), null)

    fun inject(protocol: ObjCProtocol, environment: String) = injector.inject(protocol.kotlinClass(), environment)

    fun inject(protocol: ObjCProtocol) = injector.inject(protocol.kotlinClass(), null)


    fun injectNullable(clazz: ObjCClass, environment: String) = injector.injectNullable(clazz.kotlinClass(), environment)

    fun injectNullable(clazz: ObjCClass) = injector.injectNullable(clazz.kotlinClass(), null)

    fun injectNullable(protocol: ObjCProtocol, environment: String) = injector.injectNullable(protocol.kotlinClass(), environment)

    fun injectNullable(protocol: ObjCProtocol) = injector.injectNullable(protocol.kotlinClass(), null)


    // Provide raw objects or an InstanceObjC<*> object for more information
    // If want to specify the environment, do it with Environment("value")
    fun create(clazz: ObjCClass, vararg providedInstances: Any): Any {
        val environment = providedInstances.singleOrNull { it is Environment } as? Environment
        val parameters = providedInstances.filterNot { it is Environment }.map { if (it is InstanceObjC) it.toKotlin() else it }
        return injector.create(clazz.kotlinClass(), parameters, environment?.value)
    }

    fun create(clazz: ObjCProtocol, vararg providedInstances: Any): Any {
        val environment = providedInstances.singleOrNull { it is Environment } as? Environment
        val parameters = providedInstances.filterNot { it is Environment }.map { if (it is InstanceObjC) it.toKotlin() else it }
        return injector.create(clazz.kotlinClass(), parameters, environment?.value)
    }


    fun reset() = injector.reset()

    fun purge() = injector.purge()


    private fun InstanceObjC.toKotlin(): Instance<*> {
        return when (this) {
            is InstanceObjC.InstanceObjCClass -> Instance(instance, type.kotlinClass(), environment)
            is InstanceObjC.InstanceObjCProtocol -> Instance(instance, type.kotlinClass(), environment)
        }
    }


    //If it doesn't exist, creates a ReferenceClass

    private fun ObjCClass.kotlinClass() = getOriginalKotlinClass(this) ?: ReferenceClass<Any>(this)

    private fun ObjCProtocol.kotlinClass() = getOriginalKotlinClass(this) ?: ReferenceClass<Any>(this)

}