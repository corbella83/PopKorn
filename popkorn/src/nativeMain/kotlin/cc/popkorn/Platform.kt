package cc.popkorn

import kotlin.reflect.KClass


actual typealias WeakReference<T> = kotlin.native.ref.WeakReference<T>


internal actual fun <T : Any> KClass<T>.getName() =
    qualifiedName?.substringAfterLast(".") ?: throw RuntimeException("Try to get details of a non existing class")


internal actual fun <T : Any> KClass<T>.isInterface(): Boolean {
    //return true
    return qualifiedName == "cc.popkorn.data.TestInterface" ||
            qualifiedName == "cc.popkorn.RuntimeTests.I1" ||
            qualifiedName == "cc.popkorn.RuntimeTests.I2"
}


internal actual fun <T : Any> KClass<T>.isAbstract(): Boolean {
    //return false
    return qualifiedName == "cc.popkorn.RuntimeTests.C1"
}


