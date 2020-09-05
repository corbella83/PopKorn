package cc.popkorn

import cc.popkorn.mapping.Mapping
import kotlinx.cinterop.ObjCClass


/**
 * Compatibility class to use PopKorn from ios code
 *
 * @author Pau Corbella
 * @since 2.0.0
 */


internal lateinit var classCreator: (ObjCClass) -> Mapping

/**
 * This method needs to be called on IOS platform before using PopKorn. This is because from Kotlin
 * cannot instantiate a class with "class_createInstance(ObjCClass, 0) as? Mapping". So we need
 * the ios to provide a lambda of how to create an object
 *
 * @param creator Lambda defining how to create a certain ObjCClass
 */
fun setup(creator: (ObjCClass) -> Mapping) {
    if (::classCreator.isInitialized) return
    classCreator = creator
}
