package cc.popkorn.annotations

import cc.popkorn.core.Propagation
import cc.popkorn.core.Scope
import kotlin.reflect.KClass

/**
 * Annotation to define a class that provides an injectable class
 * This is normally used with external classes, where you don't have
 * the source code to add @Injectable annotation
 * By default, only the provided class is injectable (Propagation.NONE)
 * Also, if not defined, takes the scope BY_APP by default
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class InjectableProvider(
    val scope: Scope = Scope.BY_APP,
    val alias: String = "",
    val propagation: Propagation = Propagation.NONE,
    vararg val exclude: KClass<*> = []
)

