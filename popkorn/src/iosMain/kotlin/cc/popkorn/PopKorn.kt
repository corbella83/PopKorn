package cc.popkorn

import cc.popkorn.core.Injector
import cc.popkorn.mapping.Mapping
import kotlinx.cinterop.ObjCClass
import kotlin.reflect.KClass


/**
 * PopKorn DI (ObjC)
 *
 * @author Pau Corbella
 * @since 1.6.0
 */


//TODO
// Currently "class_createInstance(ObjCClass, 0) as? Mapping" doesn't work
// so, instead,  we must init PopKorn with a creator lambda


internal lateinit var injector: Injector

fun init(creator: (ObjCClass) -> Mapping) {
    if (::injector.isInitialized) return
    injector = Injector(objcResolverPool(creator), objcProviderPool(creator))
}

fun popKorn(): InjectorController = injector


inline fun <reified T : Any> inject(environment: String? = null) = T::class.inject(environment)

fun <T : Any> KClass<T>.inject(environment: String? = null) = injector.inject(this, environment)

