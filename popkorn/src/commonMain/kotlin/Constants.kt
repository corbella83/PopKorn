package cc.popkorn

private val PROHIBITED_PACKAGES =
    arrayOf(Regex("java\\..*"), Regex("javax\\..*"), Regex("kotlin\\..*"), Regex("kotlinx\\..*"))

const val RESOLVER_SUFFIX = "Resolver"
const val PROVIDER_SUFFIX = "Provider"

const val RESOLVER_MAPPINGS = "popkorn.resolver.mappings"
const val PROVIDER_MAPPINGS = "popkorn.provider.mappings"

fun normalizeQualifiedName(path: String): String {
    val isProhibited = PROHIBITED_PACKAGES.map { path.matches(it) }.any { it }
    return if (isProhibited) "cc.popkorn.$path" else path
}
