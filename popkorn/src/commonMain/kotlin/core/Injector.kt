@file:Suppress("UNCHECKED_CAST")

package cc.popkorn.core

import cc.popkorn.InjectorController
import cc.popkorn.ParametersFactory
import cc.popkorn.core.exceptions.*
import cc.popkorn.instances.*
import cc.popkorn.needsResolver
import cc.popkorn.pools.ProviderPool
import cc.popkorn.pools.ResolverPool
import cc.popkorn.resolvers.Resolver
import cc.popkorn.resolvers.RuntimeResolver
import kotlin.reflect.KClass

/**
 * Main class to perform injections
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
class Injector(
    private val resolverPool: ResolverPool,
    private val providerPool: ProviderPool
) : InjectorController {

    internal val resolvers = hashMapOf<KClass<*>, Resolver<*>>()
    internal val instances = hashMapOf<KClass<*>, Instances<*>>()

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
    override fun <T : Any> addInjectable(instance: T, type: KClass<out T>, environment: String?) {
        if (providerPool.isPresent(type) || resolverPool.isPresent(type)) throw AlreadyInjectableException()

        if (type.needsResolver(resolverPool)) {
            resolvers.getOrPut(type, { RuntimeResolver() })
                .let { it as? RuntimeResolver }
                ?.apply { put(environment, type) }
                ?: throw AlreadyInjectableException()
        }

        instances.getOrPut(type, { RuntimeInstances<T>() })
            .let { it as? RuntimeInstances<T> }
            ?.apply { put(environment, instance) }
            ?: throw AlreadyInjectableException()
    }

    /**
     * This method allows to remove an injectable instance at runtime.
     *
     * @param type Is the type of the instance you would like to remove
     * @param environment The environment the instance is attached to
     */
    override fun <T : Any> removeInjectable(type: KClass<T>, environment: String?) {
        if (type.needsResolver(resolverPool)) {
            resolvers[type]
                ?.let { it as? RuntimeResolver }
                ?.apply { remove(environment) }
                ?.takeIf { it.size() == 0 }
                ?.also { resolvers.remove(type) }
        }

        instances[type]
            ?.let { it as? RuntimeInstances<T> }
            ?.apply { remove(environment) }
            ?.takeIf { it.size() == 0 }
            ?.also { instances.remove(type) }
    }

    /**
     * Resets all instances created by this time. This will force to recreate all instances
     * Notice that objects which already have an injected classes will maintain them until recreated.
     *
     */
    override fun reset() {
        instances.clear()
        resolvers.clear()
    }

    /**
     * Frees memory that is not needed anymore
     *
     * */
    override fun purge() {
        resolvers.filter { it.value is RuntimeResolver }
            .also {
                resolvers.clear()
                resolvers.putAll(it)
            }

        instances.mapNotNull { it.value as? Purgeable }
            .forEach { it.purge() }
    }

    /**
     * Retrieves an object of type clazz (it will be created or provided depending on its Scope)
     *
     * @param clazz Class or Interface that you want to retrieve
     * @param environment The environment in which you would like to retrieve the object
     */
    override fun <T : Any> inject(clazz: KClass<T>, environment: String?): T {
        return if (clazz.needsResolver(resolverPool)) {
            provide(clazz.getImplementation(environment), environment)
        } else {
            provide(clazz, environment)
        }
    }

    /**
     * Retrieves an object of type clazz (it will be created or provided depending on its Scope)
     * If fails getting it, will return null
     *
     * @param clazz Class or Interface that you want to retrieve
     * @param environment The environment in which you would like to retrieve the object
     */
    override fun <T : Any> injectNullable(clazz: KClass<T>, environment: String?): T? {
        return try {
            inject(clazz, environment)
        } catch (e: ProviderNotFoundException) {
            null
        } catch (e: ResolverNotFoundException) {
            null
        } catch (e: InstanceNotFoundException) {
            null
        } catch (e: DefaultImplementationNotFoundException) {
            null
        } catch (e: DefaultConstructorNotFoundException) {
            null
        } catch (e: DefaultMethodNotFoundException) {
            null
        }
    }

    /**
     * Creates an object of type clazz using given instances preferentially. If not assisting a certain parameter
     * it will be injected normally. Notice that this function will always return a new instance ignoring the scope it has.
     * It doesn't propagate. Assisted instances will only be used at the constructor of 'clazz'
     *
     * @param clazz Class or Interface that you want to create
     * @param environment The environment in which you would like to create the object
     * @param parametersFactory The instances you would like to use (preferentially) when creating the object.
     */
    override fun <T : Any> create(clazz: KClass<T>, environment: String?, parametersFactory: ParametersFactory?): T {
        val pool = if (clazz.needsResolver(resolverPool)) {
            providerPool.create(clazz.getImplementation(environment))
        } else {
            providerPool.create(clazz)
        }

        val wrapper = InjectorWrapper(this, parametersFactory ?: ParametersFactory.Builder().build())
        return pool.create(wrapper, environment)
    }

    private fun <T : Any> provide(clazz: KClass<T>, environment: String?): T {
        return instances.getOrPut(clazz, { clazz.getInstances() })
            .get(environment) as T
    }

    private fun <T : Any> KClass<T>.getImplementation(environment: String?): KClass<out T> {
        return resolvers.getOrPut(this, { resolverPool.create(this) })
            .resolve(environment) as KClass<out T>
    }

    private fun <T : Any> KClass<T>.getInstances(): Instances<T> {
        val provider = providerPool.create(this)
        return when (provider.scope()) {
            Scope.BY_APP -> PersistentInstances(this@Injector, provider)
            Scope.BY_USE -> VolatileInstances(this@Injector, provider)
            Scope.BY_NEW -> NewInstances(this@Injector, provider)
        }
    }

}
