package cc.popkorn.data

import cc.popkorn.InjectorManager
import cc.popkorn.core.Parameters
import cc.popkorn.core.Scope
import cc.popkorn.providers.Provider

class TestClassByUse(environment: String?) : TestClassNoProvider(environment)

class TestClassByUseProvider : Provider<TestClassByUse> {
    override fun create(injector: InjectorManager, assisted: Parameters, environment: String?): TestClassByUse = TestClassByUse(environment)
    override fun scope() = Scope.BY_USE
}
