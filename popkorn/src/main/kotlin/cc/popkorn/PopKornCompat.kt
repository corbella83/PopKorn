package cc.popkorn

/**
 * Compatibility class to use PopKorn from java code
 * Use it like PopKornCompat.inject(class)
 *
 * @author Pau Corbella
 * @since 1.0
 */
class PopKornCompat {

    companion object{

        @JvmStatic
        fun <T:Any> addInjectable(instance : T, type: Class<out T>, environment:String){
            injector.addInjectable(instance, type.kotlin, environment)
        }

        @JvmStatic
        fun <T:Any> addInjectable(instance : T, type: Class<out T>){
            injector.addInjectable(instance, type.kotlin)
        }

        @JvmStatic
        fun <T:Any> addInjectable(instance : T, environment:String){
            injector.addInjectable(instance, environment = environment)
        }

        @JvmStatic
        fun <T:Any> addInjectable(instance : T){
            injector.addInjectable(instance)
        }


        @JvmStatic
        fun <T:Any> removeInjectable(type: Class<T>, environment:String){
            injector.removeInjectable(type.kotlin, environment)
        }

        @JvmStatic
        fun <T:Any> removeInjectable(type: Class<T>){
            injector.removeInjectable(type.kotlin)
        }


        @JvmStatic
        fun reset(){
            injector.reset()
        }


        @JvmStatic
        fun <T:Any> inject(clazz:Class<T>, environment:String) = injector.inject(clazz.kotlin, environment)

        @JvmStatic
        fun <T:Any> inject(clazz:Class<T>) = injector.inject(clazz.kotlin, null)

    }

}