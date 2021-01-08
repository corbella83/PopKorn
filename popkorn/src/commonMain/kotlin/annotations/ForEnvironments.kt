package cc.popkorn.annotations

/**
 * Annotation to define the environments that an @Injectable class can be injected to or
 * to define the constructors to be used when injecting an @Injectable class or
 * to define the method to be used when injecting an @InjectableProvider class
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class ForEnvironments(vararg val value: String)
