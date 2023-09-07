interface IArgsLoadable{
    fun validateArgs(args: Map<String, String>)
    fun getArgLoaders(): Map<String, ArgWrapper<Any>>
}

interface ArgWrapper<T>{
    fun load(value:T)
    fun required(): Boolean
}

interface BoolArg : ArgWrapper<Boolean>
interface IntArg : ArgWrapper<Int>


object Example {
    class Concrete() : IArgsLoadable{
        var intArg: Int = 0
        var boolArg: Boolean = false
        override fun validateArgs(args: Map<String, String>) {
            TODO("Crash if argument validation fails")
        }

        override fun getArgLoaders(): Map<String, ArgWrapper<Any>> {
            return mapOf(
                    "intArg" to object: IntArg{
                        override fun required(): Boolean {
                            return true
                        }

                        override fun load(value: Int) {
                            print("Hello from \"intArg\".load")
                            this@Concrete.intArg = value
                        }
                    },
                    "boolArg" to object: BoolArg{
                        override fun required(): Boolean {
                            return true
                        }

                        override fun load(value: Boolean) {
                            print("Hello from \"boolArg\".load")
                            this@Concrete.boolArg = value
                        }
                    }

            )as Map<String, ArgWrapper<Any>>
        }

    }

    @JvmStatic
    fun main(argv: Array<String>){
        //Mimic an "unknown if args loadable" type which we'll get in full implementation
        val instance = Concrete() as Any //You should create this from the context

        //Load arguments
        val args = mapOf(
                "intArg" to "5",
                "boolArg" to "false"
        )

        if(instance is IArgsLoadable){ //Won't always be true in real project
            println("Validating args")
            instance.validateArgs(args)
            val callbacks = instance.getArgLoaders()
            val required = callbacks.filterValues { it.required() }
            val optional = callbacks.filterValues { !it.required() }
            for ((argName, delegate) in callbacks){
                println("Executing delegate for $argName...")
                when(delegate){
                    is IntArg -> {
                        //pull from args map and parse
                        delegate.load(args[argName]!!.toInt())
                    }
                    is BoolArg -> {
                        //pull from args map and parse
                        delegate.load(args[argName]!!.toBoolean())
                    }
                }
            }
        }
        instance as Concrete
        assert(instance.intArg == 5)
        assert(instance.boolArg)
    }
}