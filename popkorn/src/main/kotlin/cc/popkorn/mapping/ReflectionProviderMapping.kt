package cc.popkorn.mapping

import cc.popkorn.PROVIDER_SUFFIX
import kotlin.reflect.KClass

/**
 * Implementation of Mapping for Providers using Reflection
 *
 * @author Pau Corbella
 * @since 1.1.0
 */
class ReflectionProviderMapping : Mapping {

     override fun find(original: KClass<out Any>): Any? {
          return try {
               Class.forName("${original.java.name}_$PROVIDER_SUFFIX").newInstance()
          } catch (e:Throwable){
               null
          }
     }

}