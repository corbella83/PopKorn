package cc.popkorn.androidx.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import cc.popkorn.ParametersFactory
import cc.popkorn.androidx.viewmodel.factory.PopKornViewModelFactory

/**
 * Method to create a `ViewModel` if `ViewModelStoreOwner is the context`
 * Sample: `injectViewModel<SomeViewModel>() `
 */
inline fun <reified T : ViewModel> ViewModelStoreOwner.injectViewModel(): T {
    return ViewModelProvider(this, PopKornViewModelFactory(null)).get(T::class.java)
}

/**
 * Method to create a `ViewModel`, lazily, if `ViewModelStoreOwner is the context`
 * Sample: `by injectingViewModel<SomeViewModel>() `
 */
inline fun <reified T : ViewModel> ViewModelStoreOwner.injectingViewModel(): Lazy<T> =
    lazy { ViewModelProvider(this, PopKornViewModelFactory(null)).get(T::class.java) }

/**
 * Method to create a `ViewModel` with params if `ViewModelStoreOwner is the context`
 * Sample: `create<SomeViewModel> { add("some param") } `
 */
inline fun <reified T : ViewModel> ViewModelStoreOwner.createViewModel(
    noinline parameters: (ParametersFactory.Builder.() -> Unit)? = null,
): T {
    val params = parameters?.let { ParametersFactory.Builder().also(it).build() }
    return ViewModelProvider(this, PopKornViewModelFactory(params)).get(T::class.java)
}

/**
 * Method to create a `ViewModel` with params, lazily, if `ViewModelStoreOwner is the context`
 * Sample: `by creatingViewModel<SomeViewModel> { add("param1"); add("param2"); } `
 */
inline fun <reified T : ViewModel> ViewModelStoreOwner.creatingViewModel(
    noinline parameters: (ParametersFactory.Builder.() -> Unit)? = null,
): Lazy<T> {
    val params = parameters?.let { ParametersFactory.Builder().also(it).build() }
    return lazy { ViewModelProvider(this, PopKornViewModelFactory(params)).get(T::class.java) }
}
