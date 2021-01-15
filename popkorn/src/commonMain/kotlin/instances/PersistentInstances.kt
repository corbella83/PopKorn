package cc.popkorn.instances

import cc.popkorn.InjectorManager
import cc.popkorn.core.config.Parameters
import cc.popkorn.providers.Provider

/**
 * Instances implementation for Scope.BY_APP
 * Calling get() for the same environment and T::class will return always the same instance
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
internal class PersistentInstances<T : Any>(private val injector: InjectorManager, private val provider: Provider<T>) : Instances<T> {
    private val instances = hashMapOf<String?, T>()

    fun get(environment: String?): T {
        return instances.getOrPut(environment) { provider.create(injector, Parameters.EMPTY, environment) }
    }

    override fun size() = instances.size

}
