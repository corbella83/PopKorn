package cc.popkorn.annotations

import cc.popkorn.Scope
import kotlin.reflect.KClass

/**
 * Annotation to define that a class can be injected.
 * By default this annotation adds also as an injectable object all its interfaces.
 * If you want to exclude some of them, do it through parameter 'exclude'
 *
 * @author Pau Corbella
 * @since 1.0
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Injectable(
    val scope: Scope = Scope.BY_APP,
    val alias:String = "",
    vararg val exclude: KClass<*> = []
)

