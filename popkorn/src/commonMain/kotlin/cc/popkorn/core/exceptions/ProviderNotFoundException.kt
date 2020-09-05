package cc.popkorn.core.exceptions

import cc.popkorn.getName
import kotlin.reflect.KClass

class ProviderNotFoundException(clazz: KClass<*>) :
    RuntimeException("Could not find Provider for this class: ${clazz.getName()}. Did you forget to add @Injectable?")