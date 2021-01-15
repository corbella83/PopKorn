package cc.popkorn.data

import cc.popkorn.InjectorManager
import cc.popkorn.core.Scope
import cc.popkorn.core.config.Parameters
import cc.popkorn.providers.Provider

class TestAssistedClass(val param1: String, val param2: Int, environment: String?) : TestClassNoProvider(environment)

class TestAssistedClassProvider : Provider<TestAssistedClass> {
    override fun create(injector: InjectorManager, assisted: Parameters, environment: String?): TestAssistedClass = TestAssistedClass(assisted.get(String::class), assisted.get(Int::class), environment)
    override fun scope() = Scope.BY_NEW
}
