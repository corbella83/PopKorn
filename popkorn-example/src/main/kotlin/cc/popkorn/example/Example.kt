package cc.popkorn.example

import cc.popkorn.*
import cc.popkorn.example.model.*

class Example {

    fun execute() {
        val d10 = D10()
        popKorn().addInjectable(d10)

        popKorn().create(R1i::class) {
            assist(this@Example)
        }

        val string1 by popkorn<R2i>()
        val string2 by injecting<R2i>()

        inject<String>()
        inject<Int>()

        inject<R1i>()
        inject<R2i>()
        inject<R3i>()
        inject<R4i>("envX")
        inject<R5i>()

        System.gc()
        popKorn().purge()

        inject<R6i>()
        inject<R7i>()
        inject<R8i>()
        inject<R8i>("env1")
        inject<R8i>("env2")
        inject<R8i>("env3")
        inject<R8i>("env4")
        inject<R9i>()

        inject<M1>()
        inject<M1>("env1")

        popKorn().create(R10i::class) {
            assist(10L)
            assist(R9())
        }

        popKorn().create(R10i::class, "env2") {
            assist(10L)
            assist(15L, "second")
            assist(R10())
        }

        val c3 = popKorn().create(R8i::class)

        popKorn().create(R8i::class, "env1")

        popKorn().inject(R9i::class, "env3") {
            holder(c3)
        }

        popKorn().removeInjectable(d10::class)
        popKorn().reset()
    }

}
