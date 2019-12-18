package cc.popkorn.data

import cc.popkorn.core.Scope
import cc.popkorn.core.Injector
import cc.popkorn.providers.Provider

class TestClassByNew(environment: String?) : TestClassNoProvider(environment)

class TestClassByNew_Provider : Provider<TestClassByNew> {
    override fun create(injector: Injector, environment: String?): TestClassByNew = TestClassByNew(environment)
    override fun scope() = Scope.BY_NEW
}
