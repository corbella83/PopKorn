package cc.popkorn.compiler.utils

import com.squareup.kotlinpoet.asTypeName
import kotlinx.metadata.Flag
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import kotlinx.metadata.jvm.moduleName
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.*
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass


/**
 * @param clazz the annotation
 * @return Returns all the Elements annotated with @clazz
 */
internal fun RoundEnvironment.getAll(clazz : KClass<out Annotation>) = getElementsAnnotatedWith(clazz.java)


/**
 * @param clazz the annotation
 * @return Returns all @clazz Annotations of this Element
 */
internal fun <A:Annotation> Element.get(clazz : KClass<A>) = getAnnotation(clazz.java) ?: null


/**
 * @param clazz the annotation
 * @return Returns if this Element has the annotation @clazz
 */
internal fun Element.has(clazz : KClass<out Annotation>) = getAnnotation(clazz.java)!=null


/**
 * @param clazz the class to be compared with
 * @return Returns if this Type is the same as the type in KClass @clazz.
 *         Notice that this will compare either java class and kotlin class
 */
internal fun TypeMirror.isSame(clazz : KClass<*>) : Boolean{
    return asTypeName()==clazz.java.asTypeName() || asTypeName()==clazz.asTypeName()
}


/**
 * @return Returns if this Type is an interface
 */
internal fun Element.isInterface() = kind==ElementKind.INTERFACE


/**
 * @return Returns if this Type is internal
 */
internal fun Element.isInternal() : Boolean{
    return get(Metadata::class)
        ?.run { KotlinClassHeader(kind, metadataVersion, bytecodeVersion, data1, data2, extraString, packageName, extraInt) }
        ?.let { KotlinClassMetadata.read(it) as? KotlinClassMetadata.Class }
        ?.let { Flag.IS_INTERNAL(it.toKmClass().flags) }
        ?: false
}



/**
 * @return Returns all public constructors from this Element
 */
internal fun Element.getConstructors() : List<ExecutableElement>{
    return enclosedElements
        .filter { it.kind == ElementKind.CONSTRUCTOR && it.modifiers.contains(Modifier.PUBLIC) }
        .map{ it as ExecutableElement }
}


/**
 * Splits this qualified class into package (first) and class name (second)
 *
 * @return Returns a Pair containing the package (first) and class name (second)
 */
internal fun String.splitPackage(): Pair<String, String>{
    val split = split(".")
    return if (split.size==1){
        Pair("", split.single())
    }else{
        Pair(split.dropLast(1).joinToString("."), split.last())
    }
}

