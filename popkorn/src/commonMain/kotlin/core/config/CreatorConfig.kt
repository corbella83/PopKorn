package cc.popkorn.core.config

import kotlin.reflect.KClass

/**
 * Extra configurations to be used when creating an instance
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
class CreatorConfig private constructor(
    val assisted: Parameters,
    val overridden: Parameters
) {

    class Builder internal constructor() {
        private val assisted = Parameters.Builder()
        private val overridden = Parameters.Builder()

        fun assistAll(instances: List<Any>) =
            instances.forEach { assisted.add(it) }

        fun <P : Any> assist(instance: P, type: KClass<out P>, environment: String? = null) =
            assisted.add(instance, type, environment)

        fun assist(instance: Any, environment: String? = null) =
            assisted.add(instance, instance::class, environment)

        fun overrideAll(instances: List<Any>) =
            instances.forEach { overridden.add(it) }

        fun <P : Any> override(instance: P, type: KClass<out P>, environment: String? = null) =
            overridden.add(instance, type, environment)

        fun override(instance: Any, environment: String? = null) =
            overridden.add(instance, instance::class, environment)

        internal fun build() = CreatorConfig(assisted.build(), overridden.build())
    }
}
