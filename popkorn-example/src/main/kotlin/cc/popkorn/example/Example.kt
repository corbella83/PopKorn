package cc.popkorn.example

import cc.popkorn.example.model.*
import cc.popkorn.inject
import cc.popkorn.popKorn

fun main() {
    val d10 = D10()
    popKorn().addInjectable(d10)

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

    popKorn().removeInjectable(d10::class)
    popKorn().reset()

    println("ok")
    ExampleCompat().execute()
}