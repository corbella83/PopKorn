package cc.popkorn.mapping

import cc.popkorn.RESOLVER_SUFFIX
import kotlin.reflect.KClass

/**
 * Implementation of Mapping for Resolvers using Reflection
 *
 * @author Pau Corbella
 * @since 1.0
 */
class ReflectionResolverMapping : Mapping {

     override fun find(original: KClass<out Any>): Any? {
          return try {
               Class.forName("${original.java.name}_$RESOLVER_SUFFIX").newInstance()
          }catch (e:Throwable){
               null
          }
     }

}