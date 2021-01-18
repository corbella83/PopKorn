package cc.popkorn.example.model

import cc.popkorn.annotations.ForEnvironments
import cc.popkorn.annotations.Injectable
import cc.popkorn.core.Propagation
import cc.popkorn.core.Scope


@Target(AnnotationTarget.CLASS)
@Injectable(Scope.BY_USE, propagation = Propagation.DIRECT)
annotation class SuperAnnotation


interface M1

@SuperAnnotation
@ForEnvironments("env1")
class C1 : M1


@SuperAnnotation
class C2 : M1
