package cc.popkorn.pools

import cc.popkorn.core.Provider
import kotlin.reflect.KClass

/**
 * Interface that creates the Provider for a certain class
 *
 * @author Pau Corbella
 * @since 1.0
 */
interface ProviderPool {

     fun <T:Any> supports(clazz:KClass<T>) : Boolean

     fun <T:Any> create(clazz:KClass<T>) : Provider<T>

}