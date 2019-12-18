package cc.popkorn.instances


/**
 * Interface that defines how to get an instance from a specific environment
 *
 * @author Pau Corbella
 * @since 1.0.0
 */
internal interface Instances<T : Any> {

    fun get(environment: String?): T

    fun size(): Int

}