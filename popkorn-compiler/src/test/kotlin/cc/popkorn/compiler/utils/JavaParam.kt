package cc.popkorn.compiler.utils

/**
 * Class to create a java parameter by code
 *
 * @author Pau Corbella
 * @since 1.2.0
 */
class JavaParam(private val name: String, private val type: String) {
    private val annotations = arrayListOf<String>()

    fun alias(alias: String): JavaParam {
        this.annotations.add("@Alias(\"$alias\")")
        return this
    }

    fun withEnvironment(environment: String): JavaParam {
        this.annotations.add("@WithEnvironment(\"$environment\")")
        return this
    }

    fun construct(): String {
        return "${annotations.joinToString(" ")} $type $name"
    }

}
