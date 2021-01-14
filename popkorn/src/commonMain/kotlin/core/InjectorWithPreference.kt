package cc.popkorn.core

import cc.popkorn.InjectorManager
import cc.popkorn.core.builder.Config
import cc.popkorn.core.builder.InjectorBuilder
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

    override fun <T : Any> inject(clazz: KClass<T>, environment: String?): T {
        return injectInternal(clazz, environment, null)
    }

    override fun <T : Any> injectOrNull(clazz: KClass<T>, environment: String?): T? {
        return injectOrNullInternal(clazz, environment, null)
    }

    override fun <T : Any> willInject(clazz: KClass<T>, environment: String?): InjectorBuilder<T> {
        return InjectorBuilder({ injectInternal(clazz, environment, it) }, { injectOrNullInternal(clazz, environment, it) })
    }


    private fun <T : Any> injectInternal(clazz: KClass<T>, environment: String?, config: Config.Inject?): T {
        overridden?.getOrNull(clazz, environment)?.let { return it }

        return if (config != null) {
            baseInjector.willInject(clazz, environment).inject(config)
        } else {
            baseInjector.inject(clazz, environment)
        }
    }

    private fun <T : Any> injectOrNullInternal(clazz: KClass<T>, environment: String?, config: Config.Inject?): T? {
        overridden?.getOrNull(clazz, environment)?.let { return it }

        return if (config != null) {
            baseInjector.willInject(clazz, environment).injectOrNull(config)
        } else {
            baseInjector.injectOrNull(clazz, environment)
        }
    }

}
