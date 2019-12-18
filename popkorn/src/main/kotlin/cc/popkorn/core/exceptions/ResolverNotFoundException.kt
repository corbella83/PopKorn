package cc.popkorn.core.exceptions

import kotlin.reflect.KClass

class ResolverNotFoundException(clazz:KClass<*>) : RuntimeException("Could not find Resolver for this class: ${clazz.java.name}. Is this interface being used by an Injectable class?")