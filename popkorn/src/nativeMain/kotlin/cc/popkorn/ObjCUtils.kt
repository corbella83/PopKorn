package cc.popkorn

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


//TODO El PopClass guanya adeptes
//Hi ha dos tipus de clases a popkorn, les que es compilen automaticament,
// i les que s'afegeixen externament (addInjectable). Aquestes ultimes depenen de la plataforma,
// mentres que les primeres son sempre KClass. Per tant fer un ExternalClass que s'implementi per
// plataforma i es el que comprobara si es interficie o clase.
//LA comprobacio de si necesita resolver o no hauria de ser si (esta al resolver pool) o (es externalclass i !isClass())
//Mirar si un PopClass es pot utilitzar en comptes del KClass al Injector.
//Necesitariem un adaptador per Kotlin llabors tambe


internal fun ObjCClass.kotlinClass(): KClass<*> {
    return getOriginalKotlinClass(this) ?: MyObjCClass(this)
}

internal fun ObjCProtocol.kotlinClass(): KClass<*> {
    return getOriginalKotlinClass(this) ?: MyObjCClass(this)
}


internal fun objcResolverPool(creator: (ObjCClass) -> Mapping): ResolverPool {
    return loadMappings("ResolverMapping", creator)
        .takeIf { it.isNotEmpty() }
        ?.let { MappingResolverPool(it) }
        ?: throw RuntimeException("Could not load Resolver Mappings")
}

internal fun objcProviderPool(creator: (ObjCClass) -> Mapping): ProviderPool {
    return loadMappings("ProviderMapping", creator)
        .takeIf { it.isNotEmpty() }
        ?.let { MappingProviderPool(it) }
        ?: throw RuntimeException("Could not load Resolver Mappings")
}

private fun loadMappings(type: String, creator: (ObjCClass) -> Mapping): Set<Mapping> {
    return getMappings(type)
        .map { creator(it) }
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