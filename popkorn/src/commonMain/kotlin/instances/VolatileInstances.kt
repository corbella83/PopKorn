package cc.popkorn.instances

import cc.popkorn.InjectorManager
import cc.popkorn.WeakReference
import cc.popkorn.core.config.Parameters
import cc.popkorn.providers.Provider

/**
 * Instances implementation for Scope.BY_USE
 * Calling get() for the same environment and T::class will return the same instance as long as
 * this instance is being used by others. If no other object is using it then it will create a new one
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
internal class VolatileInstances<T : Any>(private val injector: InjectorManager, private val provider: Provider<T>) : Instances<T>, Purgeable {
    private val instances = hashMapOf<String?, WeakReference<T>>()

    fun get(environment: String?): T {
        return instances[environment]?.get() ?: provider.create(injector, Parameters.EMPTY, environment)
            .also { instances[environment] = WeakReference(it) }
    }

    override fun size() = instances.size

    override fun purge() = instances.filter { it.value.get() == null }.forEach { instances.remove(it.key) }
}
