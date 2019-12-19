package cc.popkorn.core.exceptions

class DefaultConstructorNotFoundException(element: String) : RuntimeException("Could not find constructor for default environment at $element")