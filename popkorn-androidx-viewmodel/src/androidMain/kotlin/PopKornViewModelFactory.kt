package cc.popkorn.androidx.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cc.popkorn.core.config.InjectorConfig
import cc.popkorn.popKorn

class PopKornViewModelFactory(
    private val environment: String? = null,
    private val config: (InjectorConfig.Builder.() -> Unit)?
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
        popKorn().inject(modelClass.kotlin, environment, config)
}
