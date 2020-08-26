package cc.popkorn

import cc.popkorn.core.Injector
import cc.popkorn.core.exceptions.NonExistingClassException
import cc.popkorn.core.exceptions.PopKornNotInitializedException
import cc.popkorn.mapping.Mapping
import cc.popkorn.pools.MappingProviderPool
import cc.popkorn.pools.MappingResolverPool
import cc.popkorn.pools.ProviderPool
import cc.popkorn.pools.ResolverPool
import kotlinx.cinterop.*
import platform.CoreFoundation.*
import platform.Foundation.CFBridgingRelease
import platform.Foundation.NSURL
import platform.objc.objc_copyClassNamesForImage
import platform.objc.objc_getClass
import kotlin.reflect.KClass


/**
 * Implementation for Native of the methods/classes that are Platform-dependent
 *
 * @author Pau Corbella
 * @since 1.6.0
 */

actual typealias WeakReference<T> = kotlin.native.ref.WeakReference<T>


internal actual fun <T : Any> KClass<T>.getName() = qualifiedName ?: throw NonExistingClassException(this)

internal actual fun <T : Any> KClass<T>.needsResolver(resolverPool: ResolverPool) = resolverPool.isPresent(this)

internal actual fun createDefaultInjector() = Injector(objcResolverPool(), objcProviderPool())


private fun objcResolverPool(): ResolverPool {
    return MappingResolverPool(loadMappings("ResolverMapping"))
}

private fun objcProviderPool(): ProviderPool {
    return MappingProviderPool(loadMappings("ProviderMapping"))
}

private fun loadMappings(type: String): Set<Mapping> {
    if (!::classCreator.isInitialized) throw PopKornNotInitializedException()
    return getMappings(type)
        .map { classCreator(it) }
        .toSet()
}


private fun getMappings(type: String): Set<ObjCClass> {
    val allExecutables = getAllExecutables()
    val outCount = getUIntPointer()

    val set = hashSetOf<ObjCClass>()
    for (executable in allExecutables) {
        val classes = objc_copyClassNamesForImage(executable, outCount.get()) ?: continue
        val count = outCount.get()[0].toInt()

        for (index in 0 until count) {
            val value = classes[index] as? CPointer<ByteVar>
            val name = value?.toKString() ?: continue

            if (name.endsWith(type)) {
                val real = objc_getClass(name) as? ObjCClass ?: continue
                set.add(real)
            }
        }
    }

    outCount.unpin()
    return set
}


private fun getAllExecutables(): List<String> {
    val allBundles = CFBundleGetAllBundles()

    val count = CFArrayGetCount(allBundles)
    val result = arrayListOf<String>()
    for (index in 0 until count) {
        CFArrayGetValueAtIndex(allBundles, index)
            .let { it as? CFBundleRef }
            ?.takeUnless { it.getIdentifier()?.startsWith("com.apple") ?: false }
            ?.getExecutableUrl()
            ?.also { result.add(it) }
    }

    return result
}


private fun getUIntPointer(): Pinned<CPointer<UIntVar>> {
    val memScope = MemScope()
    val rawPtr2 = memScope.alloc(sizeOf<UIntVar>(), 0).rawPtr
    val interpret = interpretCPointer<UIntVar>(rawPtr2) ?: throw RuntimeException("Could not create C pointer")
    return interpret.pin()
}

private fun CFBundleRef.getIdentifier(): String? {
    return CFBundleGetIdentifier(this)
        ?.let { CFBridgingRelease(it) as? String }
}

private fun CFBundleRef.getExecutableUrl(): String? {
    return CFBundleCopyExecutableURL(this)
        .let { CFBridgingRelease(it) as? NSURL }
        ?.fileSystemRepresentation
        ?.toKString()
}

