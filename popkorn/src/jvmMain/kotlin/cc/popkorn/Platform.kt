package cc.popkorn

import java.lang.reflect.Modifier
import kotlin.reflect.KClass


actual typealias WeakReference<T> = java.lang.ref.WeakReference<T>


internal actual fun <T : Any> KClass<T>.getName() = java.name


internal actual fun <T : Any> KClass<T>.needsResolver() = isInterface() || isAbstract()


private fun <T : Any> KClass<T>.isInterface() = this.java.isInterface

// Must check it's primitiveness because java considers them abstract (int, long, float, double, etc)
private fun <T : Any> KClass<T>.isAbstract() =
    (!this.java.isPrimitive && Modifier.isAbstract(this.java.modifiers))





