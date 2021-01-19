package cc.popkorn.annotations

/**
 * This annotation applies ONLY for interfaces and abstract classes. It annotates the desired interfaces that
 * don't want to add as injectable. Needs to be BINARY to be detected between modules
 * when compiling
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class Exclude
