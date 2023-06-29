package cc.popkorn.instances

/**
 * Interface that defines if a class is able to be purged
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
internal interface Purgeable {

    fun purge()
}
