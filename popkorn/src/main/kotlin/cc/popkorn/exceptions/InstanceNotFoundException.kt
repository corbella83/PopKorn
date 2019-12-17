package cc.popkorn.exceptions

import kotlin.reflect.KClass

class InstanceNotFoundException : RuntimeException("Invalid instance. Seems like you didn't call addInjectable")