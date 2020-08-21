package cc.popkorn

import cc.popkorn.core.Injector
import cc.popkorn.mapping.Mapping
import cc.popkorn.pools.*
import java.lang.reflect.Modifier
import kotlin.reflect.KClass

/**
 * Implementation for JVM of the methods/classes that are Platform-dependent
 *
 * @author Pau Corbella
 * @since 1.6.0
 */

actual typealias WeakReference<T> = java.lang.ref.WeakReference<T>


internal actual fun <T : Any> KClass<T>.getName() = java.name

internal actual fun <T : Any> KClass<T>.needsResolver(resolverPool: ResolverPool) = isInterface() || isAbstract()

internal actual fun createDefaultInjector() = Injector(jvmResolverPool(), jvmProviderPool())


private fun <T : Any> KClass<T>.isInterface() = this.java.isInterface

// Must check it's primitiveness because java considers them abstract (int, long, float, double, etc)
private fun <T : Any> KClass<T>.isAbstract() = (!this.java.isPrimitive && Modifier.isAbstract(this.java.modifiers))


private fun jvmResolverPool(): ResolverPool {
    return loadMappings(RESOLVER_MAPPINGS)
        .takeIf { it.isNotEmpty() }
        ?.let { MappingResolverPool(it) }
        ?: ReflectionResolverPool()
}

private fun jvmProviderPool(): ProviderPool {
    return loadMappings(PROVIDER_MAPPINGS)
        .takeIf { it.isNotEmpty() }
        ?.let { MappingProviderPool(it) }
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
                            val mapping = Class.forName(it).getDeclaredConstructor().newInstance() as Mapping
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

