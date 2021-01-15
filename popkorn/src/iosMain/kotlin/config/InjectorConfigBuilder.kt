package cc.popkorn.config

import cc.popkorn.core.config.InjectorConfig
import cc.popkorn.core.model.Instance
import cc.popkorn.kotlinClass
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.ObjCProtocol

/**
 * Extra configurations to be used when injecting an instance from ObjectiveC
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
class InjectorConfigBuilder {
    private val assisted = arrayListOf<Instance<*>>()
    private var holder: Any? = null

    fun assist(instance: Any, type: ObjCClass, environment: String): InjectorConfigBuilder {
        assisted.add(Instance(instance, type.kotlinClass(), environment))
        return this
    }

    fun assist(instance: Any, type: ObjCClass): InjectorConfigBuilder {
        assisted.add(Instance(instance, type.kotlinClass()))
        return this
    }

    fun assist(instance: Any, protocol: ObjCProtocol, environment: String): InjectorConfigBuilder {
        assisted.add(Instance(instance, protocol.kotlinClass(), environment))
        return this
    }

    fun assist(instance: Any, protocol: ObjCProtocol): InjectorConfigBuilder {
        assisted.add(Instance(instance, protocol.kotlinClass()))
        return this
    }


    fun holder(holder: Any): InjectorConfigBuilder {
        this.holder = holder
        return this
    }


    internal fun apply(builder: InjectorConfig.Builder) {
        assisted.forEach { builder.assist(it.instance, it.type, it.environment) }
        holder?.let { builder.holder(it) }
    }

}
