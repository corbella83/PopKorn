package cc.popkorn

import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.ObjCObject
import kotlinx.cinterop.ObjCProtocol
import kotlin.reflect.KClass

internal class MyObjCClass(private val objCObject: ObjCObject) : KClass<Any> {
    // TODO es el punter, no val aixo!! Hauria de ser el nom o identityhash en tot cas, comprobar un que sigui del tipus
    override val qualifiedName = objCObject.toString()
    override val simpleName = objCObject.toString()

    constructor(objClass: ObjCClass):this(objClass as ObjCObject)

    constructor(objProtocol: ObjCProtocol):this(objProtocol as ObjCObject)

    override fun equals(other: Any?): Boolean {
        return other is MyObjCClass && objCObject == other.objCObject
    }

    override fun hashCode() = simpleName.hashCode()

    override fun isInstance(value: Any?): Boolean {
        TODO("Not supported in native")
    }

    override fun toString() = "class $simpleName"

}

