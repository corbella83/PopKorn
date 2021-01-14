package cc.popkorn.core.builder

import cc.popkorn.core.Parameters
import cc.popkorn.core.model.Instance
import kotlin.reflect.KClass

class CreatorBuilder<T : Any> internal constructor(private val creator: (Config.Create) -> T) {
    private val assisted = Parameters.Builder()
    private val overridden = Parameters.Builder()

    fun overrideAll(instances: List<Any>): CreatorBuilder<T> {
        instances.map { if (it is Instance<*>) it else Instance(it) }.forEach { override(it) }
        return this
    }

    fun <P : Any> override(instance: P, type: KClass<out P>, environment: String? = null) = override(Instance(instance, type, environment))

    fun override(instance: Any, environment: String? = null) = override(instance, instance::class, environment)

    private fun override(instance: Instance<*>): CreatorBuilder<T> {
        overridden.add(instance)
        return this
    }


    fun assistedAll(instances: List<Any>): CreatorBuilder<T> {
        instances.map { if (it is Instance<*>) it else Instance(it) }.forEach { assisted(it) }
        return this
    }

    fun <P : Any> assisted(instance: P, type: KClass<out P>, environment: String? = null) = assisted(Instance(instance, type, environment))

    fun assisted(instance: Any, environment: String? = null) = assisted(instance, instance::class, environment)

    private fun assisted(instance: Instance<*>): CreatorBuilder<T> {
        assisted.add(instance)
        return this
    }


    internal fun create(config: Config.Create) = creator(config)

    fun create() = creator(Config.Create(overridden.build(), assisted.build()))

}
