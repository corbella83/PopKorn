@file:Suppress("UNCHECKED_CAST")
package cc.popkorn.pools

import cc.popkorn.core.Resolver
import kotlin.reflect.KClass

/**
 * Implementation to get implementation of an interface via reflection
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class InnerResolverPool : ResolverPool {
    private val resolvers = hashMapOf<KClass<*>, Resolver<*>>()

    override fun <T : Any> supports(clazz: KClass<T>): Boolean {
        return findResolver(clazz.java)!=null
    }

    override fun <T : Any> resolve(clazz: KClass<T>, environment: String?): KClass<out T> {
        return resolvers.getOrPut(clazz, { createResolver(clazz) })
            .resolve(environment) as KClass<out T>
    }

    private fun <T : Any> createResolver(clazz: KClass<T>): Resolver<T> {
        return findResolver(clazz.java)
            ?.newInstance()
            ?.let { it as? Resolver<T> }
            ?: throw RuntimeException("Could not find Resolver for this class: ${clazz.qualifiedName}. Is this interface being used by an Injectable class?")
    }

    private fun findResolver(original:Class<*>) : Class<*>?{
        return try { Class.forName("${original.name}_Resolver") }catch (e:ClassNotFoundException){ null }
    }

}