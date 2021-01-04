package cc.popkorn.core.exceptions

import cc.popkorn.getName
import kotlin.reflect.KClass

class AssistedNotFoundException(clazz: KClass<*>, environment: String?) : RuntimeException("Could not find instance for this class: ${clazz.getName()} ${if (environment != null) "for environment $environment" else ""}. Please provide one")