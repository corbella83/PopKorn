package cc.popkorn.compiler.utils

import cc.popkorn.PROVIDER_SUFFIX
import cc.popkorn.RESOLVER_SUFFIX
import cc.popkorn.core.Scope
import cc.popkorn.normalizeQualifiedName

/**
 * Class to create a java class by code
 *
 * @author Pau Corbella
 * @since 1.2.0
 */
class JavaClass(private val type: String = "class", private val name: String = "Test${System.nanoTime()}") {
    private val pack = "com.test"
    private val imports = arrayListOf("cc.popkorn.core.*", "cc.popkorn.annotations.*")
    private val annotations = arrayListOf<String>()
    private val modifiers = arrayListOf<String>()
    private var extends: String? = null
    private val implements = arrayListOf<String>()
    private val methods = arrayListOf<JavaMethod>()

    fun qualifiedName() = "$pack.$name"

    fun getGeneratedFile(): String? {
        return if (type == "class" && annotations.any { it.startsWith("@Injectable(") }) {
            "${qualifiedName()}_$PROVIDER_SUFFIX"
        } else if (type == "class" && annotations.any { it.startsWith("@InjectableProvider(") }) {
            return methods.mapNotNull { it.getReturnType() }.firstOrNull()?.let { "${normalizeQualifiedName(it)}_$PROVIDER_SUFFIX" }
        } else if (type == "interface") {
            "${qualifiedName()}_$RESOLVER_SUFFIX"
        } else {
            null
        }
    }


    fun import(import: String): JavaClass {
        this.imports.add(import)
        return this
    }

    fun injectable(scope: Scope? = null, alias: String? = null): JavaClass {
        val param1 = scope?.let { "scope = Scope.${it.name}" }
        val param2 = alias?.let { "alias = \"$it\"" }
        val params = arrayOf(param1, param2).filterNotNull()
        this.annotations.add("@Injectable(${params.joinToString()})")
        return this
    }

    fun injectableProvider(scope: Scope? = null, alias: String? = null): JavaClass {
        val param1 = scope?.let { "scope = Scope.${it.name}" }
        val param2 = alias?.let { "alias = \"$it\"" }
        val params = arrayOf(param1, param2).filterNotNull()
        this.annotations.add("@InjectableProvider(${params.joinToString()})")
        return this
    }

    fun forEnvironments(vararg env: String): JavaClass {
        val params = env.joinToString { "\"$it\"" }
        this.annotations.add("@ForEnvironments({$params})")
        return this
    }

    fun modifiers(vararg modifiers: String): JavaClass {
        this.modifiers.addAll(modifiers)
        return this
    }

    fun extends(clazz: String): JavaClass {
        this.extends = clazz
        return this
    }

    fun implements(vararg interfaces: String): JavaClass {
        this.implements.addAll(interfaces)
        return this
    }

    fun constructor(environments: List<String>?, vararg params: JavaParam): JavaClass {
        val javaMethod = JavaMethod(name)
        javaMethod.isConstructor()
        javaMethod.modifiers("public")
        if (environments != null) javaMethod.forEnvironments(*environments.toTypedArray())
        params.forEach { javaMethod.param(it) }
        this.methods.add(javaMethod)
        return this
    }

    fun method(method: JavaMethod): JavaClass {
        this.methods.add(method)
        return this
    }


    fun construct(): String {
        val runImports = imports.joinToString("\n") { "import $it;" }
        val runAnnotation = annotations.joinToString("\n")
        val runModifiers = modifiers.joinToString(" ")
        val runExtends = extends?.let { "extends $it" } ?: ""
        val runImplements = implements.takeIf { it.isNotEmpty() }?.let { "implements ${it.joinToString()}" } ?: ""
        val runMethods = methods.joinToString("\n\n") { it.construct() }
        return "package $pack;\n$runImports\n\n$runAnnotation\n$runModifiers $type $name $runExtends $runImplements{\n$runMethods\n}"
    }

}