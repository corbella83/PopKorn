package cc.popkorn.compiler.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import kotlinx.metadata.Flag
import kotlinx.metadata.jvm.KotlinClassHeader
import kotlinx.metadata.jvm.KotlinClassMetadata
import javax.lang.model.element.*
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass

/**
 * @param annotation the annotation
 * @return Returns annotation @annotation of this Element
 */
internal fun <A : Annotation> Element.get(annotation: KClass<A>) = getAnnotation(annotation.java) ?: null

/**
 * @param annotation the annotation
 * @return Returns if this Element has the annotation @annotation
 */
internal fun Element.has(annotation: KClass<out Annotation>) = getAnnotation(annotation.java) != null

/**
 * @return Returns if this Element is an interface
 */
internal fun Element.isInterface() = kind == ElementKind.INTERFACE

/**
 * @return Returns if this Element is an abstract class
 */
internal fun Element.isAbstract() = modifiers.contains(Modifier.ABSTRACT)

/**
 * @return Returns if this Element is an inner class
 */
internal fun Element.isInner() = enclosingElement?.kind != ElementKind.PACKAGE

/**
 * @return Returns if this Element is a private class
 */
internal fun Element.isPrivate() = !modifiers.contains(Modifier.PUBLIC) && !modifiers.contains(Modifier.PROTECTED)

/**
 * @return Returns if this Element is an internal class
 */
internal fun Element.isInternal(): Boolean {
    return get(Metadata::class)
        ?.readOrNull()
        ?.let { Flag.IS_INTERNAL(it.toKmClass().flags) }
        ?: false
}

private fun Metadata.readOrNull(): KotlinClassMetadata.Class? {
    // This should be only KotlinClassMetadata.read(this) but metadata version 0.6.0 seems not working sometimes (NoSuchMethodError)
    val header = KotlinClassHeader(kind, metadataVersion, data1, data2, extraString, packageName, extraInt)
    return KotlinClassMetadata.Companion.read(header) as? KotlinClassMetadata.Class
}

/**
 * @return Returns the name associated with this element
 */
internal fun TypeElement.getClassName(): ClassName {
    return asClassName()
}

/**
 * @return Returns the name associated with this element
 */
internal fun TypeMirror.getTypeName(): TypeName {
    return asTypeName()
}

/**
 * @return Returns all public constructors of this Element
 */
internal fun Element.getConstructors(): List<ExecutableElement> {
    return enclosedElements
        .filter { it.kind == ElementKind.CONSTRUCTOR && it.modifiers.contains(Modifier.PUBLIC) }
        .map { it as ExecutableElement }
}

/**
 * @return Returns all public methods of this Element
 */
internal fun Element.getMethods(): List<ExecutableElement> {
    return enclosedElements
        .filter { it.kind == ElementKind.METHOD && it.modifiers.contains(Modifier.PUBLIC) }
        .map { it as ExecutableElement }
}

/**
 * @return Returns if this element represents a kotlin class
 */
internal fun TypeElement.isKotlinClass() = has(Metadata::class)
