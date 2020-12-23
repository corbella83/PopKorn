package cc.popkorn.core.exceptions

class PopKornNotInitializedException : RuntimeException("You must execute PopKornCompatKt.setup(...) before using this library")