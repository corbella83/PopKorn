package cc.popkorn.core

import cc.popkorn.InjectorManager
import cc.popkorn.core.config.InjectorConfig
import cc.popkorn.core.config.Parameters
import kotlin.reflect.KClass

/**
 * Define an Injector Manager that firstly resolves dependencies based on the ones passed ('overridden').
 * If not, will resolve them with the base injector
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
internal class InjectorWithPreference(
    private val baseInjector: InjectorManager,
    private val overridden: Parameters?
) : InjectorManager {

    override fun <T : Any> inject(clazz: KClass<T>, environment: String?, config: (InjectorConfig.Builder.() -> Unit)?): T {
        return overridden?.getOrNull(clazz, environment)
            ?: baseInjector.inject(clazz, environment, config)
    }

    override fun <T : Any> injectOrNull(clazz: KClass<T>, environment: String?, config: (InjectorConfig.Builder.() -> Unit)?): T? {
        return overridden?.getOrNull(clazz, environment)
            ?: baseInjector.injectOrNull(clazz, environment, config)
    }

}
