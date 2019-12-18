package cc.popkorn.compiler.models

import javax.lang.model.element.TypeElement

/**
 * Data class defining a class and its available environments
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
internal data class DefaultImplementation(val element: TypeElement, val environments: List<String?>)