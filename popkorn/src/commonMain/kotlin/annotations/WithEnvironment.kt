package cc.popkorn.annotations

/**
 * Annotation to use a specific environment as an implementation of a constructor/method parameter
 *
 * constructor(param1:Interface, @WithEnvironment("PRO") param2:Interface)
 * this will inject the default implementation of Interface as param1 and
 * the implementation of Interface in the environment PRO as param2
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.SOURCE)
annotation class WithEnvironment(val value: String)
