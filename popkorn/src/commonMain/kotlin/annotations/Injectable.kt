package cc.popkorn.annotations

import cc.popkorn.core.Propagation
import cc.popkorn.core.Scope
import kotlin.reflect.KClass

/**
 * Annotation to define that a class can be injected.
 * By default, this annotation adds also as an injectable object all its interfaces (Propagation.ALL) unless otherwise specified
 * Also, if not defined, takes the scope BY_APP by default
 * If you want to exclude some of them, do it through parameter 'exclude'
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Injectable(
    val scope: Scope = Scope.BY_APP,
    @Deprecated("Use environments instead") val alias: String = "",
    val propagation: Propagation = Propagation.ALL,
    vararg val exclude: KClass<*> = arrayOf()
)
