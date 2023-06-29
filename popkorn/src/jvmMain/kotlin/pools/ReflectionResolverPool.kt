@file:Suppress("UNCHECKED_CAST")

package cc.popkorn.pools

import cc.popkorn.RESOLVER_SUFFIX
import cc.popkorn.core.exceptions.ResolverNotFoundException
import cc.popkorn.normalizeQualifiedName
import cc.popkorn.resolvers.Resolver
import kotlin.reflect.KClass

/**
 * Implementation to get resolvers via reflection
 *
 * @author Pau Corbella
 * @since 1.3.0
 */
internal class ReflectionResolverPool : ResolverPool {

    override fun <T : Any> isPresent(clazz: KClass<T>): Boolean {
        val name = transform(clazz)
        return existClass(name)
    }

    override fun <T : Any> create(clazz: KClass<T>): Resolver<T> {
        val name = transform(clazz)
        return createClass<T>(name)
            ?.let { it as? Resolver<T> }
            ?: throw ResolverNotFoundException(clazz)
    }

    private fun transform(original: KClass<*>): String {
        return "${normalizeQualifiedName(original.getHierarchyName())}_$RESOLVER_SUFFIX"
    }
}
