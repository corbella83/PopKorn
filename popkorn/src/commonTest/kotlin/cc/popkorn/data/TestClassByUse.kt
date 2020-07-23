package cc.popkorn.data

import cc.popkorn.core.Injector
import cc.popkorn.core.Scope
import cc.popkorn.providers.Provider

class TestClassByUse(environment: String?) : TestClassNoProvider(environment)

class TestClassByUseProvider : Provider<TestClassByUse> {
    override fun create(injector: Injector, environment: String?): TestClassByUse = TestClassByUse(environment)
    override fun scope() = Scope.BY_USE
}
