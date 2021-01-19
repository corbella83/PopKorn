package cc.popkorn.example

import cc.popkorn.annotations.ForEnvironments
import cc.popkorn.annotations.InjectableProvider
import cc.popkorn.core.Scope

@InjectableProvider(Scope.BY_APP)
class MyString {

    fun createPro(): String = "Hello Pro"

    @ForEnvironments("pre")
    fun createPre(): String = "Hello Pre"

}
