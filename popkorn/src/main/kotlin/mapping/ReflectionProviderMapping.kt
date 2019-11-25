package mapping

import kotlin.reflect.KClass

/**
 * Implementation of Mapping for Providers using Reflection
 *
 * @author Pau Corbella
 * @since 1.0
 */
class ReflectionProviderMapping : Mapping {

     override fun find(original: KClass<*>): KClass<*>? {
          return try { Class.forName("${original.java.name}_Provider").kotlin }catch (e:ClassNotFoundException){ null }
     }

}