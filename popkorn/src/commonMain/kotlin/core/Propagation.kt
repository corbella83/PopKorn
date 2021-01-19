package cc.popkorn.core

/**
 * Enum defining how far will extend injection from an Injectable class
 * NONE -> Only the class will be injectable (Default for @InjectableProvider)
 * DIRECT -> The class and its direct interfaces will be injectable
 * ALL -> The class and all its interfaces will be injectable (Default for @Injectable)
 *
 * For Example -> C1:I1   I1:I2
 *  NONE   -> Only C1 will be injectable
 *  DIRECT -> C1 and I1 will be injectable
 *  ALL    -> C1, I1 and I2 will be injectable
 *
 * @author Pau Corbella
 * @since 1.2.0
 */
enum class Propagation {
    NONE,
    DIRECT,
    ALL
}
