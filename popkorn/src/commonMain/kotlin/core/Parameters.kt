package cc.popkorn.core

import cc.popkorn.core.exceptions.AssistedNotFoundException
import cc.popkorn.core.model.Instance
import kotlin.reflect.KClass

/**
 * Class to define a list of parameters. This class is not meant to be created by users.
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
class Parameters private constructor(private val params: List<Instance<*>>) {

    class Builder {
        private val parameters = arrayListOf<Instance<*>>()

        fun <T : Any> add(instance: Instance<T>) = parameters.add(instance)

        fun <T : Any> add(instance: T, type: KClass<out T>, environment: String? = null) = add(Instance(instance, type, environment))

        fun <T : Any> add(instance: T, environment: String? = null) = add(instance, instance::class, environment)

        fun build() = Parameters(parameters)

    }

    fun <T : Any> get(type: KClass<T>, environment: String? = null): T {
        return getOrNull(type, environment) ?: throw AssistedNotFoundException(type, environment)
    }

    fun <T : Any> getOrNull(type: KClass<T>, environment: String? = null): T? {
        return params.filter { it.type == type }
            .let { list ->
                list.singleOrNull { it.environment == environment } ?: list.singleOrNull { it.environment == null }
            }
            ?.instance
            ?.let { it as? T }
    }

    companion object {
        val EMPTY = Parameters(listOf())
    }

}
