package cc.popkorn.data

import cc.popkorn.resolvers.Resolver
import kotlin.reflect.KClass

interface TestInterface{
    val value:String?
}

class TestInterface_Resolver : Resolver<TestInterface> {
    override fun resolve(environment: String?): KClass<out TestInterface> {
        return when(environment){
            "app" -> TestClassByApp::class
            "use" -> TestClassByUse::class
            "new" -> TestClassByNew::class
            else -> TestClassByApp::class
        }
    }
}
