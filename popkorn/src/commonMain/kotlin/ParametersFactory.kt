package cc.popkorn

import cc.popkorn.core.model.Instance
import kotlin.reflect.KClass

/**
 * Class to define a list of parameters. This class is not meant to be created by users.
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
class ParametersFactory private constructor(internal val params: List<Instance<*>>) {

    class Builder internal constructor() {
        private val parameters = arrayListOf<Instance<*>>()

        fun <T : Any> add(instance: Instance<T>) = parameters.add(instance)

        fun <T : Any> add(instance: T, type: KClass<out T>, environment: String? = null) = add(Instance(instance, type, environment))

        fun <T : Any> add(instance: T, environment: String? = null) = add(instance, instance::class, environment)

        internal fun build() = ParametersFactory(parameters)

    }

    internal fun <T : Any> get(type: KClass<T>, environment: String?): T? {
        return params.filter { it.type == type }
            .let { list ->
                list.singleOrNull { it.environment == environment } ?: list.singleOrNull { it.environment == null }
            }
            ?.instance
            ?.let { it as? T }
    }

}