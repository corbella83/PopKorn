package cc.popkorn.core

import cc.popkorn.InjectorController
import cc.popkorn.ParametersFactory
import cc.popkorn.core.exceptions.AssistedNotFoundException
import cc.popkorn.core.exceptions.ProviderNotFoundException
import kotlin.reflect.KClass


/**
 * Main class to perform assisted injections
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
internal class AssistedInjector(
    private val baseInjector: InjectorController,
    private val parameters: ParametersFactory
) : InjectorController {


    override fun <T : Any> addInjectable(instance: T, type: KClass<out T>, environment: String?) {
        //Nothing to do
    }

    override fun <T : Any> removeInjectable(type: KClass<T>, environment: String?) {
        //Nothing to do
    }

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

    override fun <T : Any> create(clazz: KClass<T>, environment: String?, parametersFactory: ParametersFactory?): T {
        return parameters.get(clazz, environment) ?: baseInjector.create(clazz, environment, parametersFactory)
    }

    override fun purge() {
        //Nothing to do
    }

    override fun reset() {
        //Nothing to do
    }

}