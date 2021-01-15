package cc.popkorn.data

import cc.popkorn.InjectorManager
import cc.popkorn.core.Scope
import cc.popkorn.core.config.Parameters
import cc.popkorn.providers.Provider

class TestAssistedClass2(val param1: String, val param2: String, environment: String?) : TestClassNoProvider(environment)

class TestAssistedClass2Provider : Provider<TestAssistedClass2> {
    override fun create(injector: InjectorManager, assisted: Parameters, environment: String?): TestAssistedClass2 =
        TestAssistedClass2(assisted.get(String::class, "env1"), assisted.get(String::class, "env2"), environment)

    override fun scope() = Scope.BY_NEW
}
