package cc.popkorn

import cc.popkorn.config.CreatorConfigBuilder
import cc.popkorn.config.InjectorConfigBuilder

/**
 * Compatibility class to use PopKorn from java code
 * Use it like PopKornCompat.inject(class)
 *
 * @author Pau Corbella
 * @since 1.1.0
 */
class PopKornCompat {

    companion object {
        @JvmStatic
        private val injectorJVM = InjectorJVM(popKorn())

        @JvmStatic
        fun <T : Any> addInjectable(instance: T, type: Class<out T>, environment: String) =
            injectorJVM.addInjectable(instance, type, environment)

        @JvmStatic
        fun <T : Any> addInjectable(instance: T, type: Class<out T>) =
            injectorJVM.addInjectable(instance, type)

        @JvmStatic
        fun <T : Any> addInjectable(instance: T, environment: String) =
            injectorJVM.addInjectable(instance, environment)

        @JvmStatic
        fun <T : Any> addInjectable(instance: T) =
            injectorJVM.addInjectable(instance)

        @JvmStatic
        fun <T : Any> removeInjectable(type: Class<T>, environment: String) =
            injectorJVM.removeInjectable(type, environment)

        @JvmStatic
        fun <T : Any> removeInjectable(type: Class<T>) =
            injectorJVM.removeInjectable(type)

        @JvmStatic
        fun reset() =
            injectorJVM.reset()

        @JvmStatic
        fun purge() =
            injectorJVM.purge()

        @JvmStatic
        fun <T : Any> inject(clazz: Class<T>) =
            injectorJVM.inject(clazz)

        @JvmStatic
        fun <T : Any> inject(clazz: Class<T>, environment: String) =
            injectorJVM.inject(clazz, environment)

        @JvmStatic
        fun <T : Any> inject(clazz: Class<T>, config: InjectorConfigBuilder) =
            injectorJVM.inject(clazz, config)

        @JvmStatic
        fun <T : Any> inject(clazz: Class<T>, environment: String, config: InjectorConfigBuilder) =
            injectorJVM.inject(clazz, environment, config)

        @JvmStatic
        fun <T : Any> create(clazz: Class<T>) =
            injectorJVM.create(clazz)

        @JvmStatic
        fun <T : Any> create(clazz: Class<T>, environment: String) =
            injectorJVM.create(clazz, environment)

        @JvmStatic
        fun <T : Any> create(clazz: Class<T>, config: CreatorConfigBuilder) =
            injectorJVM.create(clazz, config)

        @JvmStatic
        fun <T : Any> create(clazz: Class<T>, environment: String, config: CreatorConfigBuilder) =
            injectorJVM.create(clazz, environment, config)
    }
}
