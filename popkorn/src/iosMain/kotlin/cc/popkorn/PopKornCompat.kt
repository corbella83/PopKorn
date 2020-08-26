package cc.popkorn

import cc.popkorn.mapping.Mapping
import kotlinx.cinterop.ObjCClass


/**
 * Compatibility class to use PopKorn from ios code
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



internal lateinit var classCreator: (ObjCClass) -> Mapping

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
