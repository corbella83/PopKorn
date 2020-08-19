package cc.popkorn

import cc.popkorn.core.Injector
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

//TODO El PopClass guanya adeptes
//Hi ha dos tipus de clases a popkorn, les que es compilen automaticament,
// i les que s'afegeixen externament (addInjectable). Aquestes ultimes depenen de la plataforma,
// mentres que les primeres son sempre KClass. Per tant fer un ExternalClass que s'implementi per
// plataforma i es el que comprobara si es interficie o clase.
//LA comprobacio de si necesita resolver o no hauria de ser si (esta al resolver pool) o (es externalclass i !isClass())
//Mirar si un PopClass es pot utilitzar en comptes del KClass al Injector.
//Necesitariem un adaptador per Kotlin llabors tambe

private lateinit var classCreator: (ObjCClass) -> Mapping

/**
 * This method needs to be called on IOS platform before using PopKorn. This is because from Kotlin
 * cannot instanciate a class with "class_createInstance(ObjCClass, 0) as? Mapping". So we need
 * the ios to provide a lamdba of how to create an object
 *
 * @param creator Lambda defining how to create a certain ObjCClass
 */
fun setup(creator: (ObjCClass) -> Mapping) {
    if (::classCreator.isInitialized) return
    classCreator = creator
}



actual typealias WeakReference<T> = kotlin.native.ref.WeakReference<T>


internal actual fun <T : Any> KClass<T>.getName() = qualifiedName ?: throw RuntimeException("Try to get details of a non existing class")

internal actual fun <T : Any> KClass<T>.needsResolver(resolverPool: ResolverPool) = resolverPool.isPresent(this)

internal actual fun createDefaultInjector() = Injector(objcResolverPool(), objcProviderPool())


private fun objcResolverPool(): ResolverPool {
    return loadMappings("ResolverMapping")
        .takeIf { it.isNotEmpty() }
        ?.let { MappingResolverPool(it) }
        ?: throw RuntimeException("Could not load Resolver Mappings")
}

private fun objcProviderPool(): ProviderPool {
    return loadMappings("ProviderMapping")
        .takeIf { it.isNotEmpty() }
        ?.let { MappingProviderPool(it) }
        ?: throw RuntimeException("Could not load Resolver Mappings")
}

private fun loadMappings(type: String): Set<Mapping> {
    if (!::classCreator.isInitialized) throw RuntimeException("You must execute PlatformKt.setup() before using this library")
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
            val name = classes[index]?.toKString() ?: continue

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

