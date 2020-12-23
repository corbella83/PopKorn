package cc.popkorn.core.exceptions

class DefaultMethodNotFoundException(element: String) :
    RuntimeException("Could not find method for default environment at $element")