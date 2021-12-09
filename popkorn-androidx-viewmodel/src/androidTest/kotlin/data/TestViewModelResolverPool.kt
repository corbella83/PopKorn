package cc.popkorn.androidx.viewModel.data

import cc.popkorn.core.exceptions.ResolverNotFoundException
import cc.popkorn.pools.ResolverPool
import cc.popkorn.resolvers.Resolver
import kotlin.reflect.KClass

class TestViewModelResolverPool : ResolverPool {

    override fun <T : Any> isPresent(clazz: KClass<T>): Boolean {
        return when (clazz) {
            TestViewModelWithParams::class -> true
            else -> false
        }
    }

    override fun <T : Any> create(clazz: KClass<T>): Resolver<T> {
        return when (clazz) {
            TestViewModelWithParams::class -> TestViewModelResolver() as Resolver<T>
            else -> throw ResolverNotFoundException(clazz)
        }
    }
}
