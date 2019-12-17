package cc.popkorn.resolvers

import cc.popkorn.core.Resolver
import cc.popkorn.exceptions.InstanceNotFoundException
import kotlin.reflect.KClass

/**
 * Class to be used when resolving the runtime injection instances
 *
 * @author Pau Corbella
 * @since 1.3.0
 */
internal class RuntimeResolver : Resolver<Any> {
    private val resolvers = HashMap<String?, KClass<out Any>>()

    override fun resolve(environment: String?): KClass<out Any> {
        return resolvers[environment] ?: resolvers[null] ?: throw InstanceNotFoundException()
    }

    fun put(environment:String?, data:KClass<out Any>) = resolvers.put(environment, data)

    fun remove(environment:String?) = resolvers.remove(environment)

    fun size() = resolvers.size

}