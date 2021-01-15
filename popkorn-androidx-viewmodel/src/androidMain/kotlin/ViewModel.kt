package cc.popkorn.androidx.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import cc.popkorn.core.config.InjectorConfig

/**
 * Method to inject a `ViewModel` with params if `ViewModelStoreOwner is the context`
 *
 * Sample:
 * ```
 * val viewModel = getViewModel()
 * ```
 *
 * Sample with assisted params:
 * ```
 * val viewModel: SomeViewModel = getViewModel {
 *     assist("param1")
 *     assist("param2")
 * }
 * ```
 */
inline fun <reified T : ViewModel> ViewModelStoreOwner.getViewModel(
    environment: String? = null,
    noinline config: (InjectorConfig.Builder.() -> Unit)? = null,
): T {
    return ViewModelProvider(this, PopKornViewModelFactory(environment, config)).get(T::class.java)
}

/**
 * Method to inject a `ViewModel` with params, lazily, if `ViewModelStoreOwner is the context`
 *
 * Sample:
 * ```
 * val viewModel: SomeViewModel by viewModel()
 * ```
 *
 * Sample with assisted params:
 * ```
 * val viewModel: SomeViewModel by viewModel {
 *     assist("param1")
 *     assist("param2")
 * }
 * ```
 */
inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModel(
    environment: String? = null,
    noinline config: (InjectorConfig.Builder.() -> Unit)? = null,
): Lazy<T> {
    return lazy {
        ViewModelProvider(this, PopKornViewModelFactory(environment, config)).get(T::class.java)
    }
}
