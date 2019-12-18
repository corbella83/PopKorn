@file:Suppress("UNCHECKED_CAST")
package cc.popkorn.pools

import cc.popkorn.RESOLVER_SUFFIX
import cc.popkorn.resolvers.Resolver
import cc.popkorn.core.exceptions.ResolverNotFoundException
import kotlin.reflect.KClass

/**
 * Implementation to get resolvers via reflection
 *
 * @author Pau Corbella
 * @since 1.3.0
 */
internal class ReflectionResolverPool : ResolverPool {

    override fun <T : Any> isPresent(clazz: KClass<T>): Boolean {
        return findClass(clazz)!=null
    }

    override fun <T : Any> create(clazz: KClass<T>): Resolver<T> {
        return findClass(clazz)
            ?.newInstance()
            ?.let { it as? Resolver<T> }
            ?: throw ResolverNotFoundException(clazz)
    }

    private fun findClass(original: KClass<*>): Class<*>? {
        return try {
            Class.forName("${original.getGenerationName()}_$RESOLVER_SUFFIX")
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