package cc.popkorn.example

import cc.popkorn.Scope
import cc.popkorn.annotations.ForEnvironments
import cc.popkorn.annotations.InjectableProvider

@InjectableProvider(Scope.BY_APP)
class MyString {

    fun createPro():String = "Hello Pro"

    @ForEnvironments("pre")
    fun createPre():String = "Hello Pre"

}