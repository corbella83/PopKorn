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
               Class.forName("${original.getGenerationName()}_$RESOLVER_SUFFIX").newInstance()
          }catch (e:Throwable){
               null
          }
     }


     private fun KClass<*>.getGenerationName():String{
          val parent = java.enclosingClass
          return if (parent==null){ //If the class its on its own
               java.name
          }else{
               "${parent.name}_${java.simpleName}"
          }
     }

}