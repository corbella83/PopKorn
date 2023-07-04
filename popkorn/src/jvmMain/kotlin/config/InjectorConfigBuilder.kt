package cc.popkorn.config

import cc.popkorn.core.config.InjectorConfig
import cc.popkorn.core.model.Instance
import cc.popkorn.kotlinClass

/**
 * Extra configurations to be used when injecting an instance from JVM
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
class InjectorConfigBuilder {
    private val assisted = arrayListOf<Instance<*>>()
    private var holder: Any? = null

    fun assistAll(instances: List<Any>): InjectorConfigBuilder {
        instances.forEach { assisted.add(Instance(it)) }
        return this
    }

    fun <T : Any> assist(instance: T, type: Class<out T>, environment: String): InjectorConfigBuilder {
        assisted.add(Instance(instance, type.kotlinClass(), environment))
        return this
    }

    fun <T : Any> assist(instance: T, type: Class<out T>): InjectorConfigBuilder {
        assisted.add(Instance(instance, type.kotlinClass()))
        return this
    }

    fun <T : Any> assist(instance: T, environment: String): InjectorConfigBuilder {
        assisted.add(Instance(instance, environment = environment))
        return this
    }

    fun <T : Any> assist(instance: T): InjectorConfigBuilder {
        assisted.add(Instance(instance))
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
