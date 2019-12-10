package cc.popkorn.compiler.utils


/**
 * Class to create a java method by code
 *
 * @author Pau Corbella
 * @since 1.2.0
 */
class JavaMethod(private val name:String){
    private val annotations = arrayListOf<String>()
    private val modifiers = arrayListOf<String>()
    private val params = arrayListOf<JavaParam>()
    private var returnType:String? = null
    private var returnValue:String? = null


    fun getReturnType() : String?{
        return when(returnType){
            "boolean" -> "java.lang.Boolean"
            "int"     -> "java.lang.Integer"
            "float"   -> "java.lang.Float"
            "long"    -> "java.lang.Long"
            "double"  -> "java.lang.Double"
            else      -> returnType
        }
    }

    fun forEnvironments(vararg env:String) : JavaMethod{
        val params = env.joinToString { "\"$it\"" }
        this.annotations.add("@ForEnvironments({$params})")
        return this
    }

    fun modifiers(vararg modifiers:String) : JavaMethod{
        this.modifiers.addAll(modifiers)
        return this
    }

    fun param(param:JavaParam) : JavaMethod{
        this.params.add(param)
        return this
    }

    fun returns(type:String, value:String = "new $type()") : JavaMethod{
        this.returnType = type
        this.returnValue = value
        return this
    }

    fun isConstructor() : JavaMethod{
        this.returnType = ""
        this.returnValue = null
        return this
    }

    fun construct() : String{
        val runAnnotation = annotations.joinToString("\n")
        val runModifiers = modifiers.joinToString(" ")
        val runParams = params.joinToString{ it.construct() }
        val runRet = returnType ?: "void"
        val runReturn = returnValue?.let { "\nreturn $it;\n" } ?: ""
        return "$runAnnotation\n$runModifiers $runRet $name($runParams){$runReturn}"
    }

}