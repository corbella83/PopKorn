package cc.popkorn.exceptions

import kotlin.reflect.KClass

class ProviderNotFoundException(clazz:KClass<*>) : RuntimeException("Could not find Provider for this class: ${clazz.qualifiedName}. Did you forget to add @Injectable?")