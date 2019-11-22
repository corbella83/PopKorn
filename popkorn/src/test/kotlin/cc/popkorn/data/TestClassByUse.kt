package cc.popkorn.data

import cc.popkorn.Scope
import cc.popkorn.core.Provider

class TestClassByUse(environment: String?) : TestClassNoProvider(environment)

class TestClassByUse_Provider : Provider<TestClassByUse> {
    override fun create(environment: String?): TestClassByUse = TestClassByUse(environment)
    override fun scope() = Scope.BY_USE
}