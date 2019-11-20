package cc.popkorn.annotations

/**
 * Annotation to define a class that provides an injectable class
 * This is normally used with external classes, where you don't have
 * the source code to add @Injectable annotation
 *
 * @author Pau Corbella
 * @since 1.0
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class InjectableProvider(val alias:String = "")

