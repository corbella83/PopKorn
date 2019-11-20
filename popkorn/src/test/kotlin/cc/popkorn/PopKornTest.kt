package cc.popkorn

import cc.popkorn.core.Injector
import java.util.*
import kotlin.reflect.KClass
import kotlin.test.assertEquals

internal abstract class PopKornTest {
    private val random = Random()
    private val alphabet = StringBuffer("ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmptwxyz")


    fun randEnvironment() : String{
        val len = random.nextInt(10)
        val sb = StringBuilder(len)
        for (i in 0 until len) {
            val loc = random.nextInt(alphabet.length)
            sb.append(alphabet[loc])
        }
        return sb.toString()
    }


    fun Injector.assertNumberInstances(numberOfProviders:Int){
        assertEquals(instances.size, numberOfProviders)
    }

    fun <T:Any> Injector.assertNumberInstancesForClass(clazz: KClass<T>, numberOfInstances:Int){
        val size = instances[clazz]?.size() ?: 0
        assertEquals(size, numberOfInstances)
    }


}