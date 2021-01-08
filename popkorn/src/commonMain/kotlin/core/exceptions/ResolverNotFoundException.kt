package cc.popkorn.core.exceptions

import cc.popkorn.getName
import kotlin.reflect.KClass

class ResolverNotFoundException(clazz: KClass<*>) : RuntimeException("Could not find Resolver for this class: ${clazz.getName()}. Is this interface being used by an Injectable class?")
