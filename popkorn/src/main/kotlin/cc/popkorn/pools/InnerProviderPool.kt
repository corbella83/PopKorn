@file:Suppress("UNCHECKED_CAST")
package cc.popkorn.pools

import cc.popkorn.core.Provider
import kotlin.reflect.KClass

/**
 * Implementation to get the provider via reflection
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class InnerProviderPool : ProviderPool {

    override fun <T : Any> supports(clazz: KClass<T>): Boolean {
        return findProvider(clazz.java)!=null
    }

    override fun <T : Any> create(clazz: KClass<T>): Provider<T> {
        return findProvider(clazz.java)
            ?.newInstance()
            ?.let { it as? Provider<T> }
            ?: throw RuntimeException("Could not find Provider for this class: ${clazz.qualifiedName}. Did you forget to add @Injectable?")
    }


    private fun findProvider(original:Class<*>) : Class<*>?{
        return try { Class.forName("${original.name}_Provider") }catch (e:ClassNotFoundException){ null }
    }

}