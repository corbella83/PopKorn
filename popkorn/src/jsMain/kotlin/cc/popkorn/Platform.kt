package cc.popkorn

import kotlin.reflect.KClass


actual class WeakReference<T : Any> actual constructor(referred: T) {
    private var pointer: T? = referred

    actual fun clear() {
        pointer = null
    }

    actual fun get() = pointer

}

internal actual fun <T : Any> KClass<T>.getName() = js.name


internal actual fun <T : Any> KClass<T>.isInterface(): Boolean {
    //return true
    return js.name == "TestInterface" ||
            js.name == "RuntimeTests\$I1" ||
            js.name == "RuntimeTests\$I2"
}


internal actual fun <T : Any> KClass<T>.isAbstract(): Boolean {
    //return false
    return js.name == "RuntimeTests\$C1"
}

