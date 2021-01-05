package cc.popkorn.androidx.viewModel.data

import androidx.lifecycle.ViewModel
import cc.popkorn.InjectorManager
import cc.popkorn.core.Scope
import cc.popkorn.providers.Provider

open class TestViewModelWithParams(val name: String, val age: Int) : ViewModel()

class TestViewModelWithParamsProvider : Provider<TestViewModelWithParams> {

    override fun create(injector: InjectorManager, environment: String?): TestViewModelWithParams =
        TestViewModelWithParams(injector.inject(String::class), injector.inject(Int::class))

    override fun scope() = Scope.BY_NEW
}
