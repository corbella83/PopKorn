package cc.popkorn.instances

import cc.popkorn.core.Injector
import cc.popkorn.core.Provider
import java.lang.ref.WeakReference


/**
 * Instances implementation for Scope.BY_USE
 * Calling get() for the same environment and T::class will return the same instance as long as
 * this instance is being used by others. If no other object is using it then it will create a new one
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
internal class VolatileInstances<T:Any>(private val injector: Injector, private val provider: Provider<T>): Instances<T> {
    private val instances = HashMap<String?, WeakReference<T>>()
    
    override fun get(environment:String?) : T{
        return instances[environment]?.get() ?: provider.create(injector, environment).also { instances[environment] = WeakReference(it) }
    }

    override fun size() = instances.size

}