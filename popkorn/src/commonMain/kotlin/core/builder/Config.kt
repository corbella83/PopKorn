package cc.popkorn.core.builder

import cc.popkorn.core.Parameters

sealed class Config(val assisted: Parameters) {

    class Inject(val holder: Any? = null, assisted: Parameters = Parameters.EMPTY) : Config(assisted)

    class Create(val overridden: Parameters = Parameters.EMPTY, assisted: Parameters = Parameters.EMPTY) : Config(assisted)

}
