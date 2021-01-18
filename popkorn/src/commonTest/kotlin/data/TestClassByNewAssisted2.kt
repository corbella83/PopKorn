package cc.popkorn.data

import cc.popkorn.InjectorManager
import cc.popkorn.core.Scope
import cc.popkorn.core.config.Parameters
import cc.popkorn.providers.Provider

class TestClassByNewAssisted2(val param1: String, val param2: String, environment: String?) : TestClassNoProvider(environment)

class TestClassByNewAssisted2Provider : Provider<TestClassByNewAssisted2> {
    override fun create(injector: InjectorManager, assisted: Parameters, environment: String?): TestClassByNewAssisted2 =
        TestClassByNewAssisted2(assisted.get(String::class, "env1"), assisted.get(String::class, "env2"), environment)

    override fun scope() = Scope.BY_NEW
}
