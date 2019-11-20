@file:Suppress("UNCHECKED_CAST")
package cc.popkorn.core

import cc.popkorn.PopKornController
import cc.popkorn.Scope
import cc.popkorn.pools.InnerResolverPool
import cc.popkorn.pools.InnerProviderPool
import cc.popkorn.instances.*
import kotlin.reflect.KClass
import cc.popkorn.instances.Instances
import cc.popkorn.pools.ProviderPool
import cc.popkorn.pools.ResolverPool


/**
 * Main class to perform the injections
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class Injector : PopKornController {
    private val resolverPools = arrayListOf<ResolverPool>()
    private val providerPools = arrayListOf<ProviderPool>()

    internal val instances = hashMapOf<KClass<*>, Instances<*>>()


    init {
        //TODO version 1.1 should take all module pools to avoid proguard
        resolverPools.add(InnerResolverPool())
        providerPools.add(InnerProviderPool())
    }


    /**
     * This method allows to add an injectable instance at runtime. No scopes are defined here,
     * as you are supposed to manually control it by addInjectable/removeInjectable
     *
     * @param instance Is the instance you would like to be injectable.
     *                 It will be injectable through instance::class only
     * @param type Is the type of the instance. If no value is passed, the instance type will be taken
     *             For example, if A1:A2, by default A1 is injectable, with this parameter you can define
     *             A2 to be the injectable type
     * @param environment The specific environment to be injected to. If null,
     *                    will be injectable by all environments
     */
    override fun <T:Any> addInjectable(instance : T, type:KClass<out T>, environment:String?){
        if (type.isInPool()) throw RuntimeException("You are trying to add an injectable that is already defined")

        instances.getOrPut(type, {ProvidedInstances<T>()})
            .let { it as? ProvidedInstances<T> }
            ?.apply { put(environment, instance) }
            ?: throw RuntimeException("You are trying to add an injectable that is already defined")
    }


    /**
     * This method allows to remove an injectable instance at runtime.
     *
     * @param type Is the type of the instance you would like to remove
     * @param environment The environment the instance is attached to
     */
    override fun <T:Any> removeInjectable(type : KClass<T>, environment:String?){
        instances[type]
            ?.let { it as? ProvidedInstances<T> }
            ?.apply { remove(environment) }
            ?.takeIf { it.size()==0 }
            ?.also { instances.remove(type) }
    }


    /**
     * Resets all instances created by this time. This will force to recreate all instances
     * Notice that objects which already have an injected classes will maintain them until recreated.
     *
     */
    override fun reset(){
        instances.clear()
    }


    /**
     * Retrieves an object of type clazz (it will be created or provided depending on its Scope)
     *
     * @param clazz Class or Interface that you want to retrieve
     * @param environment The environment in which you would like to retrieve the object
     */
    fun <T:Any> inject(clazz: KClass<T>, environment:String?) : T {
        return if (clazz.isInterface()){
            val impl = clazz.getImplementation(environment)
            provide(impl, environment)
        }else{
            provide(clazz, environment)
        }
    }

    private fun <T:Any> provide(clazz: KClass<T>, environment:String?) : T{
        return instances.getOrPut(clazz, { clazz.getInstances() })
            .get(environment) as T
    }

    private fun <T: Any> KClass<T>.isInterface () =  this.java.isInterface


    private fun <T: Any> KClass<T>.getImplementation(environment:String?) : KClass<out T>{
        //FIXME getting only first
        return resolverPools.first().resolve(this, environment)
    }

    private fun <T: Any> KClass<T>.getInstances() : Instances<T>{
        //FIXME getting only first
        val provider = providerPools.first().create(this)
        return when(provider.scope()){
            Scope.BY_APP -> PersistentInstances(provider)
            Scope.BY_USE -> VolatileInstances(provider)
            Scope.BY_NEW -> NewInstances(provider)
        }
    }

    private fun <T: Any> KClass<T>.isInPool() : Boolean{
        return providerPools.any{ it.supports(this) }
    }

}