package cc.popkorn.example.model

import cc.popkorn.annotations.*
import cc.popkorn.core.Provider
import cc.popkorn.Scope

@Injectable(alias = "is1") @ForEnvironments("env1") class D1: DiA, DiG

@Injectable(alias = "is2") class D2: DiA, DiG

@Injectable(scope = Scope.BY_APP) @ForEnvironments("env1") class D3: DiB, DiG

@Injectable(scope = Scope.BY_USE) @ForEnvironments("env2") class D4: DiB

@Injectable(scope = Scope.BY_NEW) class D5: DiB

class D6: DiC

class D7: DiC, DiG

class D8: DiC

@Injectable(exclude = [DiB::class]) class D9: Wrapper.DiD, DiG, DiB

class D10: Wrapper.DiD



@InjectableProvider(alias = "is6")
@ForEnvironments("env1", "env2", "env3")
class DCustom6 : Provider<D6> {
    override fun create(environment: String?) = D6()
    override fun scope() =  Scope.BY_NEW
}

@InjectableProvider(alias = "is7")
@ForEnvironments("env4")
class DCustom7 : Provider<D7> {
    override fun create(environment: String?) = D7()
    override fun scope() =  Scope.BY_USE
}

@InjectableProvider(alias = "is8")
class DCustom8 : Provider<D8> {
    override fun create(environment: String?) = D8()
    override fun scope() =  Scope.BY_APP
}
