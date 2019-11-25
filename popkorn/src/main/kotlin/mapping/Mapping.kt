package mapping

import kotlin.reflect.KClass

/**
 * Interface that creates gets the mapping of a certain class
 *
 * @author Pau Corbella
 * @since 1.0
 */
interface Mapping {

     fun find(original:KClass<*>) : KClass<*>?

     fun isPresent(original:KClass<*>) : Boolean{
          return find(original) != null
     }

}