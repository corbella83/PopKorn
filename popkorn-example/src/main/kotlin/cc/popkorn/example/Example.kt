package cc.popkorn.example

import cc.popkorn.*
import cc.popkorn.example.model.*

fun main() {
    val d10 = D10()
    getPopKornController().addInjectable(d10)

    inject<R1i>()
    inject<R2i>()
    inject<R3i>()
    inject<R4i>("envX")
    inject<R5i>()
    inject<R6i>()
    inject<R7i>()
    inject<R8i>()
    inject<R8i>("env1")
    inject<R8i>("env2")
    inject<R8i>("env3")
    inject<R8i>("env4")

    getPopKornController().removeInjectable(d10::class)
    getPopKornController().reset()

    println("ok")
    ExampleCompat().execute()
}