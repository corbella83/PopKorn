package mapping

import kotlin.reflect.KClass

/**
 * Implementation of Mapping for Resolvers using Reflection
 *
 * @author Pau Corbella
 * @since 1.0
 */
class ReflectionResolverMapping : Mapping {

     override fun find(original: KClass<*>): KClass<*>? {
          return try { Class.forName("${original.java.name}_Resolver").kotlin }catch (e:ClassNotFoundException){ null }
     }

}