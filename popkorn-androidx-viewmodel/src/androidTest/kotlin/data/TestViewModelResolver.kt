package cc.popkorn.androidx.viewModel.data

import cc.popkorn.resolvers.Resolver
import kotlin.reflect.KClass

class TestViewModelResolver : Resolver<TestViewModelWithParams> {
    override fun resolve(environment: String?): KClass<out TestViewModelWithParams> {
        return TestViewModelWithParams::class
    }
}
