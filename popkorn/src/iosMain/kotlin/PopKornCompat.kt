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

internal lateinit var resolverMappings: Set<Mapping>
internal lateinit var providerMappings: Set<Mapping>

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


/**
 * Secondary method to initialize popkorn from IOS in case the first doesn't work
 *
 * @param resolvers All the resolver mappings that PopKorn generated
 * @param providers All the provider mappings that PopKorn generated
 */
fun setup(resolvers: Set<Mapping>, providers: Set<Mapping>) {
    if (::resolverMappings.isInitialized || ::providerMappings.isInitialized) return
    resolverMappings = resolvers
    providerMappings = providers
}
