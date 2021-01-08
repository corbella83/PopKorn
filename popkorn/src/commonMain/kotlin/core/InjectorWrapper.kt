package cc.popkorn.core

import cc.popkorn.InjectorManager
import cc.popkorn.ParametersFactory
import cc.popkorn.core.exceptions.AssistedNotFoundException
import cc.popkorn.core.exceptions.ProviderNotFoundException
import kotlin.reflect.KClass

/**
 * Define an Injector Manager that firstly resolves dependencies based on the ones passed. If not passed, will
 * resolve them with the base injector
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
internal class InjectorWrapper(
    private val baseInjector: InjectorManager,
    private val parameters: ParametersFactory
) : InjectorManager {

    override fun <T : Any> inject(clazz: KClass<T>, environment: String?): T {
        try {
            return parameters.get(clazz, environment) ?: baseInjector.inject(clazz, environment)
        } catch (e: ProviderNotFoundException) {
            throw AssistedNotFoundException(e.clazz, environment)
        }
    }

    override fun <T : Any> injectNullable(clazz: KClass<T>, environment: String?): T? {
        return parameters.get(clazz, environment) ?: baseInjector.injectNullable(clazz, environment)
    }

}
