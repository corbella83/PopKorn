package cc.popkorn

import kotlin.reflect.KClass

/**
 * Interface with the available methods to manage injections
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
interface PopKornController {

     fun <T:Any> addInjectable(instance : T, type:KClass<out T> = instance::class, environment:String?=null)

     fun <T:Any> removeInjectable(type:KClass<T>, environment:String?=null)

     fun reset()

     fun purge()

}