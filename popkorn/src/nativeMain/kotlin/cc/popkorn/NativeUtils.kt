package cc.popkorn

import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.ObjCProtocol
import kotlinx.cinterop.getOriginalKotlinClass
import platform.Foundation.NSLog
import kotlin.reflect.KClass


internal fun ObjCClass.kotlinClass() : KClass<*> {
    return getOriginalKotlinClass(this) ?: MyObjCClass(this)
}

internal fun ObjCProtocol.kotlinClass() : KClass<*> {
    return getOriginalKotlinClass(this) ?: MyObjCClass(this)
}

