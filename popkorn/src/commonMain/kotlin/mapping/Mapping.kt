package cc.popkorn.mapping

import kotlin.reflect.KClass

/**
 * Interface that maps any class to any object
 *
 * @author Pau Corbella
 * @since 1.1.0
 */
interface Mapping {

    fun find(original: KClass<out Any>): Any?

}
