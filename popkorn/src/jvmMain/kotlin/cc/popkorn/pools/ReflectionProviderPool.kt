@file:Suppress("UNCHECKED_CAST")

package cc.popkorn.pools

import cc.popkorn.PROVIDER_SUFFIX
import cc.popkorn.core.exceptions.ProviderNotFoundException
import cc.popkorn.getName
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
        val name = transform(clazz)
        return existClass(name)
    }

    override fun <T : Any> create(clazz: KClass<T>): Provider<T> {
        val name = transform(clazz)
        return createClass<T>(name)
            ?.let { it as? Provider<T> }
            ?: throw ProviderNotFoundException(clazz)
    }

    private fun transform(original: KClass<*>): String {
        return "${normalizeQualifiedName(original.getName())}_$PROVIDER_SUFFIX"
    }


}