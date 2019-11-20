package cc.popkorn.instances

/**
 * Instances implementation for manually scoped instances
 * Calling get() will return a previously added T::class. If not added or already removed,
 * will throw a RuntimeException
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class ProvidedInstances<T:Any>: Instances<T> {
    private val instances = HashMap<String?, T>()

    override fun get(environment:String?) : T{
        return instances[environment] ?: instances[null] ?: throw RuntimeException("Invalid instance. Seems like you didn't call addInjectable")
    }

    fun put(environment:String?, data:T) = instances.put(environment, data)

    fun remove(environment:String?){
        instances.remove(environment)
    }

    override fun size() = instances.size

}