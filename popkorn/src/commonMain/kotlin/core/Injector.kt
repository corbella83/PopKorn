@file:Suppress("UNCHECKED_CAST")

package cc.popkorn.core

import cc.popkorn.InjectorController
import cc.popkorn.core.config.CreatorConfig
import cc.popkorn.core.config.InjectorConfig
import cc.popkorn.core.config.Parameters
import cc.popkorn.core.exceptions.*
import cc.popkorn.createDefaultInjector
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
class Injector : InjectorController {
    private val resolverPool: ResolverPool
    private val providerPool: ProviderPool

    internal val resolvers = hashMapOf<KClass<*>, Resolver<*>>()
    internal val instances = hashMapOf<KClass<*>, Instances<*>>()

    constructor() {
        with(createDefaultInjector()) {
            this@Injector.resolverPool = this.resolverPool
            this@Injector.providerPool = this.providerPool
        }
    }

    constructor(resolverPool: ResolverPool, providerPool: ProviderPool) {
        this.resolverPool = resolverPool
        this.providerPool = providerPool
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
    override fun <T : Any> addInjectable(instance: T, type: KClass<out T>, environment: String?) {
        if (providerPool.isPresent(type) || resolverPool.isPresent(type)) throw AlreadyInjectableException()

        if (type.needsResolver(resolverPool)) {
            resolvers.getOrPut(type) { RuntimeResolver() }
                .let { it as? RuntimeResolver }
                ?.apply { put(environment, type) }
                ?: throw AlreadyInjectableException()
        }

        instances.getOrPut(type) { RuntimeInstances<T>() }
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
     * @param config Some extra configuration needed when injecting the object.
     */
    override fun <T : Any> inject(clazz: KClass<T>, environment: String?, config: (InjectorConfig.Builder.() -> Unit)?): T {
        val configuration = config?.let { InjectorConfig.Builder().apply(it).build() }

        val resolved = clazz.resolve(environment) as KClass<T>
        return instances.getOrPut(resolved) { resolved.createInstances() }
            .let { it as Instances<T> }
            .get(resolved, environment, configuration)
    }

    /**
     * Retrieves an object of type clazz (it will be created or provided depending on its Scope)
     * If fails getting it, will return null
     *
     * @param clazz Class or Interface that you want to retrieve
     * @param environment The environment in which you would like to retrieve the object
     * @param config Some extra configuration needed when injecting the object.
     */
    override fun <T : Any> injectOrNull(clazz: KClass<T>, environment: String?, config: (InjectorConfig.Builder.() -> Unit)?): T? {
        return try {
            inject(clazz, environment, config)
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
     * Creates an object of type clazz. Notice that this function will always return a new instance
     * ignoring the scope it has.
     *
     * @param clazz Class or Interface that you want to create
     * @param environment The environment in which you would like to create the object
     * @param config Some extra configuration needed when creating the object.
     */
    override fun <T : Any> create(clazz: KClass<T>, environment: String?, config: (CreatorConfig.Builder.() -> Unit)?): T {
        val configuration = config?.let { CreatorConfig.Builder().apply(it).build() }

        val injector = configuration?.overridden?.let { InjectorWithPreference(this, it) } ?: this
        return clazz.resolve(environment)
            .let { providerPool.create(it) }
            .create(injector, configuration?.assisted ?: Parameters.EMPTY, environment)
    }

    private fun <T : Any> KClass<T>.resolve(environment: String?): KClass<out T> {
        return if (this.needsResolver(resolverPool)) {
            resolvers.getOrPut(this) { resolverPool.create(this) }
                .resolve(environment) as KClass<out T>
        } else {
            this
        }
    }

    private fun <T : Any> Instances<T>.get(clazz: KClass<T>, environment: String?, config: InjectorConfig?): T {
        return when (this) {
            is RuntimeInstances -> get(environment)
            is PersistentInstances -> get(environment)
            is VolatileInstances -> get(environment)
            is HolderInstances -> get(config?.holder ?: throw HolderNotProvidedException(clazz), environment)
            is NewInstances -> get(config?.assisted ?: Parameters.EMPTY, environment)
            else -> throw RuntimeException("Should not happen")
        }
    }

    private fun <T : Any> KClass<T>.createInstances(): Instances<T> {
        val provider = providerPool.create(this)
        return when (provider.scope()) {
            Scope.BY_APP -> PersistentInstances(this@Injector, provider)
            Scope.BY_USE -> VolatileInstances(this@Injector, provider)
            Scope.BY_HOLDER -> HolderInstances(this@Injector, provider)
            Scope.BY_NEW -> NewInstances(this@Injector, provider)
        }
    }
}
