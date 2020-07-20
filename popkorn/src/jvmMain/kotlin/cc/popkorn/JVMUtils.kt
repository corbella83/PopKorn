package cc.popkorn

import cc.popkorn.core.Injector
import cc.popkorn.mapping.Mapping
import cc.popkorn.pools.*
import kotlin.reflect.KClass


internal fun jvmResolverPool(): ResolverPool {
    return loadMappings(RESOLVER_MAPPINGS)
        .takeIf { it.isNotEmpty() }
        ?.let { ResourcesResolverPool(it) }
        ?: ReflectionResolverPool()
}

internal fun jvmProviderPool(): ProviderPool {
    return loadMappings(PROVIDER_MAPPINGS)
        .takeIf { it.isNotEmpty() }
        ?.let { ResourcesProviderPool(it) }
        ?: ReflectionProviderPool()
}


private fun loadMappings(resource: String): Set<Mapping> {
    val set = hashSetOf<Mapping>()
    Injector::class.java.classLoader.getResources("META-INF/$resource")
        .iterator()
        .forEach { url ->
            try {
                val mappers = url.readText()
                mappers.replace("\n", "")
                    .split(";")
                    .filter { it.isNotEmpty() }
                    .forEach {
                        try {
                            val mapping = Class.forName(it).newInstance() as Mapping
                            set.add(mapping)
                            //println("Successfully mapping loaded : ${mapping.javaClass}")
                        } catch (e: Exception) {
                            //println("Warning: PopKorn mapping ($it) could not be loaded. Might not work if using obfuscation")
                        }
                    }

            } catch (e: Exception) {
                //println("Warning: Some PopKorn mappings could not be loaded. Might not work if using obfuscation")
            }
        }
    return set
}


internal fun <T : Any> KClass<T>.getHierarchyName(): String {
    val parent = java.enclosingClass
    return if (parent == null) { //If the class its on its own
        java.name
    } else {
        "${parent.name}_${java.simpleName}"
    }
}


internal fun existClass(fullName: String): Boolean {
    return try {
        Class.forName(fullName)
        true
    } catch (e: Throwable) {
        false
    }
}

internal fun <T : Any> createClass(fullName: String): T? {
    return try {
        Class.forName(fullName).newInstance() as T
    } catch (e: Throwable) {
        null
    }
}
