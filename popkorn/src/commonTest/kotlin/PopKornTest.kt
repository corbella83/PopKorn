package cc.popkorn

import cc.popkorn.core.Injector
import cc.popkorn.instances.*
import kotlin.random.Random
import kotlin.reflect.KClass
import kotlin.test.assertEquals

/**
 * Parent class of all injectable tests
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
internal abstract class PopKornTest {
    private val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmptwxyz".toList()

    fun randEnvironment(): String {
        val len = Random.nextInt(10)
        val sb = StringBuilder(len)
        for (i in 0 until len) {
            val loc = Random.nextInt(alphabet.size)
            sb.append(alphabet[loc])
        }
        return sb.toString()
    }

    fun Injector.assertNumberInstances(numberOfProviders: Int) {
        assertEquals(instances.size, numberOfProviders)
    }

    fun <T : Any> Injector.assertNumberInstancesForClass(clazz: KClass<T>, numberOfInstances: Int) {
        val size = instances[clazz]?.size() ?: 0
        assertEquals(size, numberOfInstances)
    }


    private fun Instances<*>.size(): Int {
        return when (this) {
            is RuntimeInstances -> size()
            is PersistentInstances -> size()
            is VolatileInstances -> size()
            is HolderInstances -> size()
            is NewInstances -> 0
            else -> throw RuntimeException("Should not happen")
        }
    }

}
