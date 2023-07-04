package cc.popkorn.compiler.utils

import javax.annotation.processing.Messager
import javax.tools.Diagnostic

/**
 * Logger to print compile time messages
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
internal class Logger(private val messenger: Messager) {

    fun message(text: String) {
        messenger.printMessage(Diagnostic.Kind.NOTE, "PopKorn: $text")
    }

    fun warning(text: String) {
        messenger.printMessage(Diagnostic.Kind.WARNING, "PopKorn: $text")
    }

    fun error(text: String, exception: Throwable?) {
        messenger.printMessage(Diagnostic.Kind.ERROR, "PopKorn: $text")
        exception?.printStackTrace()
    }
}
