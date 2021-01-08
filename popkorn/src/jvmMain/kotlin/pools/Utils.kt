package cc.popkorn.pools

import kotlin.reflect.KClass

internal fun <T : Any> KClass<T>.getHierarchyName(): String {
    val parent = java.enclosingClass
    return if (parent == null) { // If the class its on its own
        java.name
    } else {
        "${parent.name}_${java.simpleName}"
    }
}

internal fun existClass(fullName: String): Boolean {
    return try {
        Class.forName(fullName)
        true
    } catch (e: Throwable) {
        false
    }
}

internal fun <T : Any> createClass(fullName: String): T? {
    return try {
        Class.forName(fullName).getDeclaredConstructor().newInstance() as T
    } catch (e: Throwable) {
        null
    }
}
