package cc.popkorn.exceptions

import kotlin.reflect.KClass

class ResolverNotFoundException(clazz:KClass<*>) : RuntimeException("Could not find Resolver for this class: ${clazz.qualifiedName}. Is this interface being used by an Injectable class?")