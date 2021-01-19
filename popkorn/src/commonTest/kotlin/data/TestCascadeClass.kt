package cc.popkorn.data

import cc.popkorn.InjectorManager
import cc.popkorn.core.Scope
import cc.popkorn.core.config.Parameters
import cc.popkorn.providers.Provider

class TestCascadeClass(val param1: TestClassByApp, val param2: TestClassByUse, environment: String?) : TestClassNoProvider(environment)

class TestCascadeClassProvider : Provider<TestCascadeClass> {
    override fun create(injector: InjectorManager, assisted: Parameters, environment: String?): TestCascadeClass =
        TestCascadeClass(injector.inject(TestClassByApp::class), injector.inject(TestClassByUse::class), environment)

    override fun scope() = Scope.BY_USE
}
