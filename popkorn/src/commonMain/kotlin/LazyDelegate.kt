package cc.popkorn

/**
 * Delegate to replicate the behaviour of the default 'by lazy'
 *
 * @author Pau Corbella
 * @since 2.1.0
 */
class LazyDelegate<T : Any>(val initializer: () -> T) : Lazy<T> {
    private lateinit var instance: T

    override val value: T
        get() {
            if (!::instance.isInitialized) instance = initializer()
            return instance
        }

    override fun isInitialized(): Boolean = ::instance.isInitialized

}
