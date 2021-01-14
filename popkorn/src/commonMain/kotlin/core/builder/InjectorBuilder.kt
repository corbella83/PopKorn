package cc.popkorn.core.builder

import cc.popkorn.core.Parameters
import cc.popkorn.core.model.Instance
import kotlin.reflect.KClass

class InjectorBuilder<T : Any> internal constructor(private val injector: (Config.Inject) -> T, private val nullableInjector: (Config.Inject) -> T?) {
    private val assisted = Parameters.Builder()
    private var holder: Any? = null

    fun holder(holder: Any): InjectorBuilder<T> {
        this.holder = holder
        return this
    }

    fun assistedAll(instances: List<Any>): InjectorBuilder<T> {
        instances.map { if (it is Instance<*>) it else Instance(it) }.forEach { assisted(it) }
        return this
    }

    fun <P : Any> assisted(instance: P, type: KClass<out P>, environment: String? = null) = assisted(Instance(instance, type, environment))

    fun assisted(instance: Any, environment: String? = null) = assisted(instance, instance::class, environment)

    private fun assisted(instance: Instance<*>): InjectorBuilder<T> {
        assisted.add(instance)
        return this
    }

    internal fun inject(config: Config.Inject) = injector(config)

    internal fun injectOrNull(config: Config.Inject) = nullableInjector(config)

    fun inject() = injector(Config.Inject(holder, assisted.build()))

    fun injectOrNull() = nullableInjector(Config.Inject(holder, assisted.build()))

}