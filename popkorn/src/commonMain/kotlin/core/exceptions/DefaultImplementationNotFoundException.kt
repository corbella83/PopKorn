package cc.popkorn.core.exceptions

class DefaultImplementationNotFoundException(element: String, options: List<String>) : RuntimeException("Could not find default implementation of $element: ${options.joinToString()}")