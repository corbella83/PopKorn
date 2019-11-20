package cc.popkorn.annotations

/**
 * This annotation applies ONLY for interfaces. It annotates the desired interfaces that
 * don't want to add as injectable
 *
 * @author Pau Corbella
 * @since 1.0
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class Exclude

