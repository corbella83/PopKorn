package cc.popkorn

import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.ObjCProtocol
import platform.Foundation.NSStringFromClass
import kotlin.reflect.KClass

/**
 * Fake Kotlin Class to be used when adding platform dependency objects to PopKorn
 *
 * @author Pau Corbella
 * @since 1.6.0
 */
internal class MyObjCClass private constructor(
    override val qualifiedName: String,
    override val simpleName: String
) : KClass<Any> {

    constructor(objClass: ObjCClass) : this(NSStringFromClass(objClass), NSStringFromClass(objClass))

    //TODO should be the name of the protocol
    constructor(objProtocol: ObjCProtocol) : this(objProtocol.toString(), objProtocol.toString())

    override fun equals(other: Any?): Boolean {
        return other is MyObjCClass && qualifiedName == other.qualifiedName
    }

    override fun hashCode() = qualifiedName.hashCode()

    override fun isInstance(value: Any?): Boolean {
        TODO("Not supported in native")
    }

    override fun toString() = "class $qualifiedName"

}

