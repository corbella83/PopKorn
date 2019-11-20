package cc.popkorn

import cc.popkorn.core.Injector
import kotlin.reflect.KClass


/**
 * PopKorn DI
 *
 * @author Pau Corbella
 * @since 1.0
 */


internal val injector = Injector()

fun getPopKornController() : PopKornController = injector


inline fun <reified T:Any> inject(environment : String?=null) = T::class.inject(environment)

fun <T:Any> KClass<T>.inject(environment : String?=null) = injector.inject(this, environment)
