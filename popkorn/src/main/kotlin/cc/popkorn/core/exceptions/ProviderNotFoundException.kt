package cc.popkorn.core.exceptions

import kotlin.reflect.KClass

class ProviderNotFoundException(clazz: KClass<*>) : RuntimeException("Could not find Provider for this class: ${clazz.java.name}. Did you forget to add @Injectable?")