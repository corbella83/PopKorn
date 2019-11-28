@file:Suppress("UNCHECKED_CAST")
package cc.popkorn.core

import cc.popkorn.PROVIDER_MAPPINGS
import cc.popkorn.PopKornController
import cc.popkorn.RESOLVER_MAPPINGS
import cc.popkorn.Scope
import cc.popkorn.instances.*
import kotlin.reflect.KClass
import cc.popkorn.instances.Instances
import cc.popkorn.mapping.Mapping
import cc.popkorn.pools.ProviderPool
import cc.popkorn.pools.ResolverPool
import cc.popkorn.mapping.ReflectionProviderMapping
import cc.popkorn.mapping.ReflectionResolverMapping
import org.apache.commons.io.IOUtils


/**
 * Main class to perform the injections
 *
 * @author Pau Corbella
 * @since 1.0
 */
internal class Injector(private val debug:Boolean=false) : PopKornController {
    private val resolverPool = ResolverPool()
    private val providerPool = ProviderPool()

    internal val instances = hashMapOf<KClass<*>, Instances<*>>()


    init {
        loadMappings(RESOLVER_MAPPINGS).forEach { resolverPool.addMapping(it) }
        resolverPool.addMapping(ReflectionResolverMapping())

        loadMappings(PROVIDER_MAPPINGS).forEach { providerPool.addMapping(it) }
        providerPool.addMapping(ReflectionProviderMapping())
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
        if (providerPool.isPresent(type)) throw RuntimeException("You are trying to add an injectable that is already defined")

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
        return resolverPool.resolve(this, environment)
    }

    private fun <T: Any> KClass<T>.getInstances() : Instances<T>{
        val provider = providerPool.create(this)
        return when(provider.scope()){
            Scope.BY_APP -> PersistentInstances(provider)
            Scope.BY_USE -> VolatileInstances(provider)
            Scope.BY_NEW -> NewInstances(provider)
        }
    }


    private fun loadMappings(resource:String) : List<Mapping>{
        val list = arrayListOf<Mapping>()
        javaClass.classLoader.getResources("META-INF/$resource")
            .iterator()
            .forEach { url ->
                try {
                    val mappers = IOUtils.toString(url, "UTF-8")
                    mappers.replace("\n", "")
                        .split(";")
                        .filter { it.isNotEmpty() }
                        .forEach {
                            try {
                                val mapping = Class.forName(it).newInstance() as Mapping
                                list.add(mapping)
                                if (debug) println("Successfully mapping loaded : ${mapping.javaClass}")
                            } catch (e: Exception) {
                                if (debug) println("Warning: PopKorn mapping ($it) could not be loaded. Might not work if using obfuscation")
                            }
                        }

                }catch (e:Exception){
                    if (debug) println("Warning: Some PopKorn mappings could not be loaded. Might not work if using obfuscation")
                }
            }
        return list
    }

}