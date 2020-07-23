package cc.popkorn

import cc.popkorn.pools.ResolverPool
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * Implementation for JVM of the methods/classes that are Platform-dependent
 *
 * @author Pau Corbella
 * @since 1.6.0
 */

actual typealias WeakReference<T> = java.lang.ref.WeakReference<T>


internal actual fun <T : Any> KClass<T>.getName() = java.name


internal actual fun <T : Any> KClass<T>.needsResolver(resolverPool: ResolverPool) = isInterface() || isAbstract()


private fun <T : Any> KClass<T>.isInterface() = this.java.isInterface

// Must check it's primitiveness because java considers them abstract (int, long, float, double, etc)
private fun <T : Any> KClass<T>.isAbstract() =
    (!this.java.isPrimitive && Modifier.isAbstract(this.java.modifiers))





