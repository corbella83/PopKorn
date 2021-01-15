package cc.popkorn.config

import cc.popkorn.core.config.CreatorConfig
import cc.popkorn.core.model.Instance
import cc.popkorn.kotlinClass
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.ObjCProtocol

/**
 * Extra configurations to be used when creating an instance from ObjectiveC
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
class CreatorConfigBuilder {
    private val assisted = arrayListOf<Instance<*>>()
    private val overridden = arrayListOf<Instance<*>>()

    fun assist(instance: Any, type: ObjCClass, environment: String): CreatorConfigBuilder {
        assisted.add(Instance(instance, type.kotlinClass(), environment))
        return this
    }

    fun assist(instance: Any, type: ObjCClass): CreatorConfigBuilder {
        assisted.add(Instance(instance, type.kotlinClass()))
        return this
    }

    fun assist(instance: Any, protocol: ObjCProtocol, environment: String): CreatorConfigBuilder {
        assisted.add(Instance(instance, protocol.kotlinClass(), environment))
        return this
    }

    fun assist(instance: Any, protocol: ObjCProtocol): CreatorConfigBuilder {
        assisted.add(Instance(instance, protocol.kotlinClass()))
        return this
    }


    fun override(instance: Any, type: ObjCClass, environment: String): CreatorConfigBuilder {
        overridden.add(Instance(instance, type.kotlinClass(), environment))
        return this
    }

    fun override(instance: Any, type: ObjCClass): CreatorConfigBuilder {
        overridden.add(Instance(instance, type.kotlinClass()))
        return this
    }

    fun override(instance: Any, protocol: ObjCProtocol, environment: String): CreatorConfigBuilder {
        overridden.add(Instance(instance, protocol.kotlinClass(), environment))
        return this
    }

    fun override(instance: Any, protocol: ObjCProtocol): CreatorConfigBuilder {
        overridden.add(Instance(instance, protocol.kotlinClass()))
        return this
    }


    internal fun apply(builder: CreatorConfig.Builder) {
        assisted.forEach { builder.assist(it.instance, it.type, it.environment) }
        overridden.forEach { builder.override(it.instance, it.type, it.environment) }
    }

}
