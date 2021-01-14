package cc.popkorn.example

import cc.popkorn.*
import cc.popkorn.core.model.Environment
import cc.popkorn.example.model.*

class Example {

    fun execute() {
        val d10 = D10()
        popKorn().addInjectable(d10)

        popKorn().willCreate(R1i::class)
            .assisted(this)
            .create()

        val string1 by popkorn<R2i>()
        val string2 by injecting<R2i>()
        val string3 by creating<R2i>("", 33)

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

        create<R10i> {
            assisted(10L)
            assisted(R9())
        }
        val c1 by creating<R10i>(10L, R9())

        create<R10i>("env2") {
            assisted(10L)
            assisted(15L, "second")
            assisted(R10())
        }
        val c2 by creating<R10i>(Environment("env2"), 10L, R10())

        create<R8i>()
        val c3 by creating<R8i>()

        create<R8i>("env1")
        val c4 by creating<R8i>("env1")

        popKorn().willInject(R9i::class, "env3").holder(c3).inject()

        popKorn().removeInjectable(d10::class)
        popKorn().reset()
    }

}
