package cc.popkorn.mapping

import cc.popkorn.ALTERNATE_JAVA_LANG_PACKAGE
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
               val name = "${original.java.name}_$PROVIDER_SUFFIX".replace(ALTERNATE_JAVA_LANG_PACKAGE.first, ALTERNATE_JAVA_LANG_PACKAGE.second)
               Class.forName(name).newInstance()
          } catch (e:Throwable){
               null
          }
     }

}