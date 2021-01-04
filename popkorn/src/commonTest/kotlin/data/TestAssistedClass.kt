package cc.popkorn.data

import cc.popkorn.InjectorController
import cc.popkorn.core.Scope
import cc.popkorn.providers.Provider

class TestAssistedClass(val param1: String, val param2: Int, environment: String?) : TestClassNoProvider(environment)

class TestAssistedClassProvider : Provider<TestAssistedClass> {
    override fun create(injector: InjectorController, environment: String?): TestAssistedClass = TestAssistedClass(injector.inject(String::class), injector.inject(Int::class), environment)
    override fun scope() = Scope.BY_NEW
}
