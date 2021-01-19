package cc.popkorn.annotations

/**
 * Annotation to use a specific implementation (defined in any injectable class) in a constructor/method parameter
 *
 * constructor(param1:Interface, @Alias("name") param2:Interface)
 * this will inject the default implementation of Interface as param1 and
 * "name" implementation of Interface as param2
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
@Deprecated("Use environments instead")
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class Alias(val value: String)
