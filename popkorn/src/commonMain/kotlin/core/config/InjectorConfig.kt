package cc.popkorn.core.config

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
            instances.forEach { assist(it) }

        fun <P : Any> assist(instance: P, type: KClass<out P>, environment: String? = null) =
            assisted.add(instance, type, environment)

        fun assist(instance: Any, environment: String? = null) =
            assisted.add(instance, instance::class, environment)

        fun holder(holder: Any) {
            this.holder = holder
        }

        internal fun build() = InjectorConfig(assisted.build(), holder)
    }
}
