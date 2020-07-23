package cc.popkorn

import kotlin.reflect.KClass


/**
 * Implementation for Native of the methods/classes that are Platform-dependent
 *
 * @author Pau Corbella
 * @since 1.6.0
 */

actual typealias WeakReference<T> = kotlin.native.ref.WeakReference<T>


internal actual fun <T : Any> KClass<T>.getName() = qualifiedName ?: throw RuntimeException("Try to get details of a non existing class")


//internal actual fun <T : Any> KClass<T>.needsResolver() = false

internal actual fun <T : Any> KClass<T>.needsResolver() : Boolean {
    return qualifiedName == "cc.popkorn.data.TestInterface" ||
            qualifiedName == "cc.popkorn.RuntimeTests.I1" ||
            qualifiedName == "cc.popkorn.RuntimeTests.I2" ||
            qualifiedName == "cc.popkorn.RuntimeTests.C1"
}