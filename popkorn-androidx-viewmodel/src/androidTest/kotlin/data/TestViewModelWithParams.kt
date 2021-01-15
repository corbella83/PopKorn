package cc.popkorn.androidx.viewModel.data

import androidx.lifecycle.ViewModel
import cc.popkorn.InjectorManager
import cc.popkorn.core.Scope
import cc.popkorn.core.config.Parameters
import cc.popkorn.providers.Provider

open class TestViewModelWithParams(val name: String, val age: Int) : ViewModel()

class TestViewModelWithParamsProvider : Provider<TestViewModelWithParams> {

    override fun create(
        injector: InjectorManager,
        assisted: Parameters,
        environment: String?
    ): TestViewModelWithParams {
        return TestViewModelWithParams(assisted.get(String::class), assisted.get(Int::class))
    }

    override fun scope() = Scope.BY_NEW
}
