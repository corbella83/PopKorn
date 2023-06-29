package cc.popkorn.instances

import cc.popkorn.core.exceptions.InstanceNotFoundException

/**
 * Instances implementation for manually scoped instances
 * Calling get() will return a previously added T::class. If not added or already removed,
 * will throw an InstanceNotFoundException
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
internal class RuntimeInstances<T : Any> : Instances<T> {
    private val instances = hashMapOf<String?, T>()

    fun get(environment: String?): T {
        return instances[environment] ?: instances[null] ?: throw InstanceNotFoundException()
    }

    fun put(environment: String?, data: T) = instances.put(environment, data)

    fun remove(environment: String?) {
        instances.remove(environment)
    }

    override fun size() = instances.size
}
