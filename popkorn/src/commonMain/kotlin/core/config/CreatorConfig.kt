package cc.popkorn.core.config

import cc.popkorn.core.model.Instance
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
            instances.map { if (it is Instance<*>) it else Instance(it) }.forEach { assist(it) }

        fun <P : Any> assist(instance: P, type: KClass<out P>, environment: String? = null) =
            assist(Instance(instance, type, environment))

        fun assist(instance: Any, environment: String? = null) =
            assist(instance, instance::class, environment)

        private fun assist(instance: Instance<*>) =
            assisted.add(instance)


        fun overrideAll(instances: List<Any>) =
            instances.map { if (it is Instance<*>) it else Instance(it) }.forEach { override(it) }

        fun <P : Any> override(instance: P, type: KClass<out P>, environment: String? = null) =
            override(Instance(instance, type, environment))

        fun override(instance: Any, environment: String? = null) =
            override(instance, instance::class, environment)

        private fun override(instance: Instance<*>) =
            overridden.add(instance)

        internal fun build() = CreatorConfig(assisted.build(), overridden.build())

    }

}
