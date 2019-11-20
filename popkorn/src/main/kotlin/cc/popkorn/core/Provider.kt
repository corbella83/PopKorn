package cc.popkorn.core

import cc.popkorn.Scope

/**
 * Interface that defines how a certain class can be created
 * T cannot be an interface, only classes
 *
 * @author Pau Corbella
 * @since 1.0
 */
interface Provider<T:Any> {

     fun create(environment:String?) : T

     fun scope() : Scope

}