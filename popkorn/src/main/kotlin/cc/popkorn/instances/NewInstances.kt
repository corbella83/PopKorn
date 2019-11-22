package cc.popkorn.instances

import cc.popkorn.core.Provider


/**
 * Instances implementation for Scope.BY_NEW
 * Doesn't matter the number of times get() is called, it will always return a new instance
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class NewInstances<T:Any>(private val provider: Provider<T>): Instances<T> {

    override fun get(environment:String?) : T{
        return provider.create(environment)
    }

    override fun size() = 0

}