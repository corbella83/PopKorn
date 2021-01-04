package cc.popkorn.providers

import cc.popkorn.InjectorManager
import cc.popkorn.core.Scope

/**
 * Interface that defines how a certain class can be created
 * T cannot be an interface, only classes
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
interface Provider<T : Any> {

    fun create(injector: InjectorManager, environment: String?): T

    fun scope(): Scope

}