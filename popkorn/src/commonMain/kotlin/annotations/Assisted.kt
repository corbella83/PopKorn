package cc.popkorn.annotations

/**
 * Annotation to be used in any constructor parameter that is not injectable and will be provided in
 * runtime (Can only be used in a class with BY_NEW scope).
 *
 * constructor(@Assisted id:Long, param1:Interface)
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Assisted
