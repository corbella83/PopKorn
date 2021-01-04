package cc.popkorn.data

import cc.popkorn.InjectorManager
import cc.popkorn.core.Scope
import cc.popkorn.providers.Provider

open class TestClassByApp(environment: String?) : TestClassNoProvider(environment)

class TestClassByAppProvider : Provider<TestClassByApp> {
    override fun create(injector: InjectorManager, environment: String?): TestClassByApp = TestClassByApp(environment)
    override fun scope() = Scope.BY_APP
}
