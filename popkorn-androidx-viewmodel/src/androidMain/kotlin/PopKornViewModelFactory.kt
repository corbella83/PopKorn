package cc.popkorn.androidx.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cc.popkorn.ParametersFactory
import cc.popkorn.popKorn

class PopKornViewModelFactory(private val parametersFactory: ParametersFactory?) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
       popKorn().create(modelClass.kotlin, parametersFactory)
}
