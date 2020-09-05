package cc.popkorn

import cc.popkorn.core.Injector
import cc.popkorn.core.exceptions.NonExistingClassException
import cc.popkorn.core.exceptions.PopKornNotInitializedException
import cc.popkorn.pools.MappingProviderPool
import cc.popkorn.pools.MappingResolverPool
import cc.popkorn.pools.ProviderPool
import cc.popkorn.pools.ResolverPool
import kotlin.reflect.KClass


/**
 * Implementation for Native of the methods/classes that are Platform-dependent
 *
 * @author Pau Corbella
 * @since 2.0.0
 */


actual typealias WeakReference<T> = kotlin.native.ref.WeakReference<T>

internal actual fun <T : Any> KClass<T>.getName() = this.qualifiedName ?: throw NonExistingClassException(this)

internal actual fun <T : Any> KClass<T>.needsResolver(resolverPool: ResolverPool) = resolverPool.isPresent(this)

internal actual fun createDefaultInjector() = Injector(nativeResolverPool(), nativeProviderPool())


private fun nativeResolverPool(): ResolverPool {
    if (!::iResolverMappings.isInitialized || !::iProviderMappings.isInitialized) throw PopKornNotInitializedException()
    return MappingResolverPool(iProviderMappings)
}

private fun nativeProviderPool(): ProviderPool {
    if (!::iResolverMappings.isInitialized || !::iProviderMappings.isInitialized) throw PopKornNotInitializedException()
    return MappingProviderPool(iProviderMappings)
}
