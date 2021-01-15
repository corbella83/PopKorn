package cc.popkorn.core.config

import cc.popkorn.core.model.Instance
import kotlin.reflect.KClass

/**
 * Extra configurations to be used when injecting an instance
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
class InjectorConfig private constructor(
    val assisted: Parameters,
    val holder: Any?
) {

    class Builder internal constructor() {
        private val assisted = Parameters.Builder()
        private var holder: Any? = null

        fun assistAll(instances: List<Any>) =
            instances.map { if (it is Instance<*>) it else Instance(it) }.forEach { assist(it) }

        fun <P : Any> assist(instance: P, type: KClass<out P>, environment: String? = null) =
            assist(Instance(instance, type, environment))

        fun assist(instance: Any, environment: String? = null) =
            assist(instance, instance::class, environment)

        private fun assist(instance: Instance<*>) =
            assisted.add(instance)

        fun holder(holder: Any) {
            this.holder = holder
        }

        internal fun build() = InjectorConfig(assisted.build(), holder)

    }

}
