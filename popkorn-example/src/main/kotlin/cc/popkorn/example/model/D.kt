package cc.popkorn.example.model

import cc.popkorn.annotations.ForEnvironments
import cc.popkorn.annotations.Injectable
import cc.popkorn.annotations.InjectableProvider
import cc.popkorn.core.Propagation
import cc.popkorn.core.Scope
import cc.popkorn.core.model.Environment

@Injectable(alias = "is1", propagation = Propagation.DIRECT)
@ForEnvironments("env1")
class D1 : DiA, DiG

@Injectable(alias = "is2", propagation = Propagation.DIRECT)
class D2 : DiA, DiG

@Injectable(scope = Scope.BY_APP)
@ForEnvironments("env1")
class D3 : DiB, DiG

@Injectable(scope = Scope.BY_USE)
@ForEnvironments("env2")
class D4 : DiB

@Injectable(scope = Scope.BY_NEW)
class D5 : DiB

class D6 : DiC

class D7 : DiC, DiG

class D8 : DiC

@Injectable(exclude = [DiB::class])
class D9 : Wrapper.DiD, DiG, DiB

class D10 : Wrapper.DiD

open class D11 : DaI(), DiE

@Injectable(propagation = Propagation.ALL)
class D12 : D11(), DiF

class D13 : DaJ(), DiF

@Injectable(propagation = Propagation.DIRECT)
class D14 : DaJ()

@Injectable(propagation = Propagation.NONE)
class D15 : DiF

@InjectableProvider(scope = Scope.BY_NEW, propagation = Propagation.DIRECT, alias = "is6")
@ForEnvironments("env1", "env2", "env3")
class DCustom6 {
    fun create(d0: DiA) = D6()

    @ForEnvironments("env2")
    fun createEnv2() = D6()

}

@InjectableProvider(scope = Scope.BY_USE, propagation = Propagation.DIRECT, alias = "is7")
@ForEnvironments("env4")
class DCustom7 {
    fun create(environment: Environment) = D7()
}

@InjectableProvider(scope = Scope.BY_APP, propagation = Propagation.DIRECT, alias = "is8")
class DCustom8 {
    fun create() = D8()
}

@InjectableProvider
@ForEnvironments("env9", "env2")
class DCustom9 {
    fun create(d0: DiA): DaJ = D13()

    @ForEnvironments("env2")
    fun createEnv2(): DaJ = D13()

}
