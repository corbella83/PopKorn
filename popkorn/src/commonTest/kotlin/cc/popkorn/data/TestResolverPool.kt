package cc.popkorn.data

import cc.popkorn.pools.ResolverPool
import cc.popkorn.resolvers.Resolver
import kotlin.reflect.KClass

class TestResolverPool : ResolverPool {

    override fun <T : Any> isPresent(clazz: KClass<T>): Boolean {
        return when (clazz) {
            TestInterface::class -> true
            else -> false
        }
    }

    override fun <T : Any> create(clazz: KClass<T>): Resolver<T> {
        return when (clazz) {
            TestInterface::class -> TestInterface_Resolver() as Resolver<T>
            else -> throw RuntimeException("Resolver not found : $clazz")
        }
    }
}