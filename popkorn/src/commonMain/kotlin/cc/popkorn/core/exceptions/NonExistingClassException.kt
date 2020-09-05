package cc.popkorn.core.exceptions

import kotlin.reflect.KClass

class NonExistingClassException(clazz: KClass<*>) :
    RuntimeException("Tried to get details of a non existing class: $clazz")