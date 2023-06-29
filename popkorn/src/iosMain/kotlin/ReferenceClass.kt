package cc.popkorn

import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.ObjCProtocol
import platform.Foundation.NSStringFromClass
import kotlin.reflect.KClass

/**
 * Fake KClass to hold platform-specific types
 *
 * @author Pau Corbella
 * @since 2.0.0
 */
internal class ReferenceClass<T : Any> private constructor(
    override val qualifiedName: String,
    override val simpleName: String
) : KClass<T> {

    constructor(objClass: ObjCClass) : this(NSStringFromClass(objClass), NSStringFromClass(objClass))

    // TODO should be the name of the protocol
    constructor(objProtocol: ObjCProtocol) : this(objProtocol.hashCode().toString(), objProtocol.hashCode().toString())

    override fun equals(other: Any?): Boolean {
        return other is ReferenceClass<*> && qualifiedName == other.qualifiedName
    }

    override fun hashCode() = qualifiedName.hashCode()

    override fun isInstance(value: Any?) = false

    override fun toString() = "class $qualifiedName"
}
