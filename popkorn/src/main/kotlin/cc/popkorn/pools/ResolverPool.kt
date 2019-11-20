package cc.popkorn.pools

import kotlin.reflect.KClass

/**
 * Interface that gets the implementation of a certain interface
 *
 * @author Pau Corbella
 * @since 1.0
 */
interface ResolverPool {

     fun <T:Any> supports(clazz:KClass<T>) : Boolean

     fun <T:Any> resolve(clazz:KClass<T>, environment:String?) : KClass<out T>

}