package cc.popkorn.data

import cc.popkorn.core.exceptions.ResolverNotFoundException
import cc.popkorn.pools.ResolverPool
import cc.popkorn.resolvers.Resolver
import kotlin.reflect.KClass

/**
 * ResolverPool that define how test interfaces are resolved
 *
 * @author Pau Corbella
 * @since 2.0.0
 */
class TestResolverPool : ResolverPool {

    override fun <T : Any> isPresent(clazz: KClass<T>): Boolean {
        return when (clazz) {
            TestInterface::class -> true
            else -> false
        }
    }

    override fun <T : Any> create(clazz: KClass<T>): Resolver<T> {
        return when (clazz) {
            TestInterface::class -> TestInterfaceResolver() as Resolver<T>
            else -> throw ResolverNotFoundException(clazz)
        }
    }
}
