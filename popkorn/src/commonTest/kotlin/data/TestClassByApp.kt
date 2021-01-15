package cc.popkorn.data

import cc.popkorn.InjectorManager
import cc.popkorn.core.Scope
import cc.popkorn.core.config.Parameters
import cc.popkorn.providers.Provider

open class TestClassByApp(environment: String?) : TestClassNoProvider(environment)

class TestClassByAppProvider : Provider<TestClassByApp> {
    override fun create(injector: InjectorManager, assisted: Parameters, environment: String?): TestClassByApp = TestClassByApp(environment)
    override fun scope() = Scope.BY_APP
}
