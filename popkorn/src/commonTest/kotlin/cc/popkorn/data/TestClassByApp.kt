package cc.popkorn.data

import cc.popkorn.core.Injector
import cc.popkorn.core.Scope
import cc.popkorn.providers.Provider

open class TestClassByApp(environment: String?) : TestClassNoProvider(environment)

class TestClassByApp_Provider : Provider<TestClassByApp> {
    override fun create(injector: Injector, environment: String?): TestClassByApp = TestClassByApp(environment)
    override fun scope() = Scope.BY_APP
}
