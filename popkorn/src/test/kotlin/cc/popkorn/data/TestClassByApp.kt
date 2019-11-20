package cc.popkorn.data

import cc.popkorn.Scope
import cc.popkorn.core.Provider

open class TestClassByApp(environment: String?) : TestClassNoProvider(environment)

class TestClassByApp_Provider : Provider<TestClassByApp> {
    override fun create(environment: String?): TestClassByApp = TestClassByApp(environment)
    override fun scope() = Scope.BY_APP
}
