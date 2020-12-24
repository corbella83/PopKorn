package cc.popkorn.data

import cc.popkorn.InjectorController
import cc.popkorn.core.Scope
import cc.popkorn.providers.Provider

class TestClassByUse(environment: String?) : TestClassNoProvider(environment)

class TestClassByUseProvider : Provider<TestClassByUse> {
    override fun create(injector: InjectorController, environment: String?): TestClassByUse = TestClassByUse(environment)
    override fun scope() = Scope.BY_USE
}
