package cc.popkorn.config

import cc.popkorn.core.config.CreatorConfig
import cc.popkorn.core.model.Instance
import cc.popkorn.kotlinClass

/**
 * Extra configurations to be used when creating an instance from JS
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
class CreatorConfigBuilder {
    private val assisted = arrayListOf<Instance<*>>()
    private val overridden = arrayListOf<Instance<*>>()

    fun assistAll(instances: List<Any>): CreatorConfigBuilder {
        instances.forEach { assisted.add(Instance(it)) }
        return this
    }

    fun <T : Any> assist(instance: T, type: JsClass<out T>, environment: String): CreatorConfigBuilder {
        assisted.add(Instance(instance, type.kotlinClass(), environment))
        return this
    }

    fun <T : Any> assist(instance: T, type: JsClass<out T>): CreatorConfigBuilder {
        assisted.add(Instance(instance, type.kotlinClass()))
        return this
    }

    fun <T : Any> assist(instance: T, environment: String): CreatorConfigBuilder {
        assisted.add(Instance(instance, environment = environment))
        return this
    }

    fun <T : Any> assist(instance: T): CreatorConfigBuilder {
        assisted.add(Instance(instance))
        return this
    }

    fun overrideAll(instances: List<Any>): CreatorConfigBuilder {
        instances.forEach { overridden.add(Instance(it)) }
        return this
    }

    fun <T : Any> override(instance: T, type: JsClass<out T>, environment: String): CreatorConfigBuilder {
        overridden.add(Instance(instance, type.kotlinClass(), environment))
        return this
    }

    fun <T : Any> override(instance: T, type: JsClass<out T>): CreatorConfigBuilder {
        overridden.add(Instance(instance, type.kotlinClass()))
        return this
    }

    fun <T : Any> override(instance: T, environment: String): CreatorConfigBuilder {
        overridden.add(Instance(instance, environment = environment))
        return this
    }

    fun <T : Any> override(instance: T): CreatorConfigBuilder {
        overridden.add(Instance(instance))
        return this
    }

    internal fun apply(builder: CreatorConfig.Builder) {
        assisted.forEach { builder.assist(it.instance, it.type, it.environment) }
        overridden.forEach { builder.override(it.instance, it.type, it.environment) }
    }
}
