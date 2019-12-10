package cc.popkorn.core

import kotlin.reflect.KClass

/**
 * Interface that defines how a certain interface can be resolved
 * T must be an interface, while 'out T' must be a class
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
interface Resolver<T:Any> {

    fun resolve(environment:String?) : KClass<out T>

}