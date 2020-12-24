package cc.popkorn.core

import cc.popkorn.InjectorController
import cc.popkorn.core.exceptions.AssistedNotFoundException
import cc.popkorn.core.exceptions.ProviderNotFoundException
import cc.popkorn.core.model.Instance
import cc.popkorn.pools.MappingProviderPool
import cc.popkorn.pools.MappingResolverPool
import kotlin.reflect.KClass


/**
 * Main class to perform assisted injections
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
internal class AssistedInjector(
    private val baseInjector: InjectorController,
    private val assistedInstances: List<Instance<*>>
) : InjectorController {
    private val assistedInjector = Injector(MappingResolverPool(setOf()), MappingProviderPool(setOf()))

    init {
        assistedInstances.forEach { assistedInjector.addInjectable(it.instance, it.type, it.environment) }
    }

    override fun <T : Any> addInjectable(instance: T, type: KClass<out T>, environment: String?) {
        assistedInjector.addInjectable(instance, type, environment)
    }

    override fun <T : Any> removeInjectable(type: KClass<T>, environment: String?) {
        assistedInjector.removeInjectable(type, environment)
    }

    override fun <T : Any> inject(clazz: KClass<T>, environment: String?): T {
        try {
            return assistedInjector.injectNullable(clazz, environment) ?: baseInjector.inject(clazz, environment)
        } catch (e: ProviderNotFoundException) {
            throw AssistedNotFoundException(e.clazz)
        }
    }

    override fun <T : Any> injectNullable(clazz: KClass<T>, environment: String?): T? {
        return assistedInjector.injectNullable(clazz, environment) ?: baseInjector.injectNullable(clazz, environment)
    }

    override fun <T : Any> create(clazz: KClass<T>, assistedInstances: List<Any>, environment: String?): T {
        return assistedInjector.injectNullable(clazz, environment) ?: baseInjector.create(clazz, assistedInstances, environment)
    }

    override fun purge() {
        assistedInjector.purge()
    }

    override fun reset() {
        assistedInjector.reset()
    }

}