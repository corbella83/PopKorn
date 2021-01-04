package cc.popkorn.example.model

import cc.popkorn.InjectorManager
import cc.popkorn.annotations.*
import cc.popkorn.core.model.Empty
import cc.popkorn.core.model.Environment

@Injectable
class R1(d1: D1, d2: D2, d3: D3, d4: D4) : R1i

@Injectable
class R2(d1: D5, d2: D6, d3: D7, d4: D8, d5: D9) : R2i

@Injectable
class R3(env: Environment, @WithEnvironment("env1") d1: DiA, @WithEnvironment("envX") d2: DiA, d3: DiA, @WithEnvironment("env9") d4: DaJ) : R3i

@Injectable
class R4(ign: Empty, @WithEnvironment("env1") d1: DiB, @WithEnvironment("env2") d2: DiB, d3: DiB) : R4i

@Injectable
class R5(str: String, int: Int, test: Float?, @WithEnvironment("env1") d1: DiC, @WithEnvironment("env2") d2: DiC, @WithEnvironment("env3") d3: DiC, d4: DiC) : R5i

@Injectable
class R6(@WithEnvironment("env4") d1: DiC, @WithEnvironment("env4") d2: DiC, @Alias("is7") d3: DiC, @Alias("is8") d4: DiC, d5: D12) : R6i

@Injectable
class R7(injector: InjectorManager, tmp: R0i?, d1: Wrapper.DiD, @WithEnvironment("envX") d2: Wrapper.DiD, d3: D10, d4: DaH) : R7i

@Injectable
@ForEnvironments("env1", "env2", "env3", "env4")
class R8A private constructor() : R8i {

    @ForEnvironments("env3")
    constructor(@Alias("is1") d1: DiG, @Alias("is2") d2: DiG) : this()

    @ForEnvironments("env1", "env4")
    constructor(d1: DiA, @WithEnvironment("env2") d2: DiB, d3: DiC, d4: Wrapper.DiD) : this()

    constructor(d1: D3, d2: D5, d3: D7, d4: D10) : this()

}

@Injectable
class R8B : R8i

class R9 : R9i
class R10 : R9i

@Injectable
class R11(id: Long, r1: R9, d1: D3, d2: D15) : R10i

@Injectable
@ForEnvironments("env2")
class R12(id: Long, @WithEnvironment("second") id2: Long, r1: R10, d1: D5, d2: D15) : R10i

@InjectableProvider
class RCustom {
    fun create(d0: DiA): R9i = R9()

}
