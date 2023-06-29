package cc.popkorn.example

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        Example().execute()
        println("ok kotlin")

        ExampleJava().execute()
        println("ok java")
    }
}
