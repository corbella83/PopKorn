package cc.popkorn.core.config

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

    internal class Builder {

        private val parameters = arrayListOf<Instance<*>>()

        fun <T : Any> add(instance: T, type: KClass<out T>, environment: String? = null) = parameters.add(Instance(instance, type, environment))

        fun <T : Any> add(instance: T, environment: String? = null) = parameters.add(Instance(instance, instance::class, environment))

        fun build() = Parameters(parameters)

    }

    fun <T : Any> get(clazz: KClass<T>, environment: String? = null): T {
        return params.filter { it.type == clazz }
            .let { list ->
                list.singleOrNull { it.environment == environment } ?: list.singleOrNull { it.environment == null }
            }
            ?.instance
            ?.let { it as T }
            ?: throw AssistedNotFoundException(clazz, environment)
    }

    fun <T : Any> getOrNull(clazz: KClass<T>, environment: String? = null): T? {
        return try {
            get(clazz, environment)
        } catch (e: AssistedNotFoundException) {
            null
        }
    }

    companion object {
        internal val EMPTY = Parameters(listOf())
    }

}
