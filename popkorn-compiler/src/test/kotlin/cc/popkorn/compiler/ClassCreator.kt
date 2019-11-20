package cc.popkorn.compiler

import cc.popkorn.Scope

class ClassCreator(
    private val type:String = "public class",
    private val name:String = "Test${System.nanoTime()}",
    private val superclass:String? = null,
    private val interfaces:List<String>? = null,
    private val injectable:Boolean = true,
    private val injectableAlias:String? = null,
    private val injectableScope:Scope? = null,
    private val environments:List<String>? = null) {

    fun isPublicClass() : Boolean{
        return type == "public class"
    }

    fun simpleName() : String{
        return name
    }

    fun name() : String{
        return "cc.popkorn.compiler.$name"
    }

    fun construct() : String{
        return  "package cc.popkorn.compiler;\n" +
                "\n" +
                "import cc.popkorn.Scope;\n" +
                "import cc.popkorn.annotations.ForEnvironments;\n" +
                "import cc.popkorn.annotations.Injectable;\n" +
                "\n" +
                "${getInjectableCode()}\n" +
                "${getEnvironmentsCode()}\n" +
                "$type $name ${getSuperClasses()} {\n" +
                "}\n"
    }



    private fun getInjectableCode():String?{
        return if (injectable){
            val aliasCode = injectableAlias?.let { "alias = \"$it\"" }
            val scopeCode = injectableScope?.let { "scope = \"$it\"" }
            val options = arrayOf(aliasCode, scopeCode).filterNotNull()
            "@Injectable(${options.joinToString()})"
        }else{
            ""
        }
    }


    private fun getEnvironmentsCode():String?{
        return environments?.map { "\"$it\"" }?.let { "@ForEnvironments({${it.joinToString()}})" } ?: ""
    }


    private fun getSuperClasses():String?{
        val sup = superclass?.let { "extends $it " } ?: ""
        val int = interfaces?.takeIf { it.isNotEmpty() }?.let { "implements ${it.joinToString()}" } ?: ""
        return sup + int
    }





}