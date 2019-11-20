package cc.popkorn.instances

import cc.popkorn.core.Provider


/**
 * Instances implementation for Scope.BY_APP
 * Calling get() for the same environment and T::class will return always the same instance
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class PersistentInstances<T:Any>(private val provider: Provider<T>): Instances<T> {
    private val instances = HashMap<String?, T>()

    override fun get(environment:String?) : T{
        return instances.getOrPut(environment) { provider.create(environment) }
    }

    override fun size() = instances.size

}