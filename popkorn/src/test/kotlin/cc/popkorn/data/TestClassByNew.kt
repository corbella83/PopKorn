package cc.popkorn.data

import cc.popkorn.Scope
import cc.popkorn.core.Provider

class TestClassByNew(environment: String?) : TestClassNoProvider(environment)

class TestClassByNew_Provider : Provider<TestClassByNew> {
    override fun create(environment: String?): TestClassByNew = TestClassByNew(environment)
    override fun scope() = Scope.BY_NEW
}
