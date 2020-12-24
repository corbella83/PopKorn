package cc.popkorn.core.exceptions

import cc.popkorn.getName
import kotlin.reflect.KClass

class AssistedNotFoundException(clazz: KClass<*>) : RuntimeException("Could not find assisted instance for this class: ${clazz.getName()}. Please provide one")