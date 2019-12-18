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
        messenger.printMessage(Diagnostic.Kind.NOTE, text)
    }

    fun warning(text: String) {
        messenger.printMessage(Diagnostic.Kind.WARNING, text)
    }

    fun error(text: String) {
        messenger.printMessage(Diagnostic.Kind.ERROR, text)
    }

}