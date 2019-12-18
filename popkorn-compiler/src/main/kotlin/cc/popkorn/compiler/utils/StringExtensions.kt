package cc.popkorn.compiler.utils


/**
 * Splits this qualified class into package (first) and class name (second)
 *
 * @return Returns a Pair containing the package (first) and class name (second)
 */
internal fun String.splitPackage(): Pair<String, String> {
    val split = split(".")
    return if (split.size == 1) {
        Pair("", split.single())
    } else {
        Pair(split.dropLast(1).joinToString("."), split.last())
    }
}
