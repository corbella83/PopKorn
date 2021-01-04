package cc.popkorn.data

import cc.popkorn.InjectorManager
import cc.popkorn.core.Scope
import cc.popkorn.providers.Provider

class TestClassByNew(environment: String?) : TestClassNoProvider(environment)

class TestClassByNewProvider : Provider<TestClassByNew> {
    override fun create(injector: InjectorManager, environment: String?): TestClassByNew = TestClassByNew(environment)
    override fun scope() = Scope.BY_NEW
}
