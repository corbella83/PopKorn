package cc.popkorn.instances

import cc.popkorn.InjectorManager
import cc.popkorn.WeakReference
import cc.popkorn.core.config.Parameters
import cc.popkorn.providers.Provider

/**
 * Instances implementation for Scope.BY_HOLDER
 * Calling get() for the same holder, environment and T::class will return the same instance as long as
 * the holder still exist. If holder not existing, will create a new instance
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
internal class HolderInstances<T : Any>(private val injector: InjectorManager, private val provider: Provider<T>) : Instances<T>, Purgeable {
    private val instances = hashMapOf<WeakReference<*>, HashMap<String?, T>>()

    fun get(holder: Any, environment: String?): T {
        val map = instances.mapKeys { it.key.get() }[holder]
        return if (map == null) {
            val instance = provider.create(injector, Parameters.EMPTY, environment)
            instances[WeakReference(holder)] = hashMapOf(environment to instance)
            instance
        } else {
            map[environment] ?: provider.create(injector, Parameters.EMPTY, environment).also { map[environment] = it }
        }
    }

    override fun size() = instances.size

    override fun purge() = instances.filter { it.key.get() == null }.forEach { instances.remove(it.key) }

}
