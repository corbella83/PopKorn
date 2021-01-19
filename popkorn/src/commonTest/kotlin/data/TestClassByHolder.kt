package cc.popkorn.data

import cc.popkorn.InjectorManager
import cc.popkorn.core.Scope
import cc.popkorn.core.config.Parameters
import cc.popkorn.providers.Provider

class TestClassByHolder(environment: String?) : TestClassNoProvider(environment)

class TestClassByHolderProvider : Provider<TestClassByHolder> {
    override fun create(injector: InjectorManager, assisted: Parameters, environment: String?): TestClassByHolder = TestClassByHolder(environment)
    override fun scope() = Scope.BY_HOLDER
}
