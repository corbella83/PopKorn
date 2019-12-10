package cc.popkorn.annotations

import cc.popkorn.Scope
import kotlin.reflect.KClass

/**
 * Annotation to define a class that provides an injectable class
 * This is normally used with external classes, where you don't have
 * the source code to add @Injectable annotation
 * Also, if not defined, takes the scope BY_APP by default
 *
 * @author Pau Corbella
 * @since 1.0
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class InjectableProvider(
    val scope: Scope = Scope.BY_APP,
    val alias:String = "",
    vararg val exclude: KClass<*> = []
)

