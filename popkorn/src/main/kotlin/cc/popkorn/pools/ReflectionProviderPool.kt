@file:Suppress("UNCHECKED_CAST")

package cc.popkorn.pools

import cc.popkorn.PROVIDER_SUFFIX
import cc.popkorn.core.exceptions.ProviderNotFoundException
import cc.popkorn.normalizeQualifiedName
import cc.popkorn.providers.Provider
import kotlin.reflect.KClass

/**
 * Implementation to get providers via reflection
 *
 * @author Pau Corbella
 * @since 1.3.0
 */
internal class ReflectionProviderPool : ProviderPool {

    override fun <T : Any> isPresent(clazz: KClass<T>): Boolean {
        return findClass(clazz) != null
    }

    override fun <T : Any> create(clazz: KClass<T>): Provider<T> {
        return findClass(clazz)
            ?.newInstance()
            ?.let { it as? Provider<T> }
            ?: throw ProviderNotFoundException(clazz)
    }

    private fun findClass(original: KClass<*>): Class<*>? {
        return try {
            val name = "${normalizeQualifiedName(original.java.name)}_$PROVIDER_SUFFIX"
            Class.forName(name)
        } catch (e: Throwable) {
            null
        }
    }


}