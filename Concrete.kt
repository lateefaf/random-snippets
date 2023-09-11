// Concrete class for demonstration
class Concrete : IArgsLoadable<Concrete> {
    var intArg: Int = 0
    var doubleArg: Double = 0.0

    override fun getArgs(): Map<String, ArgDefinition<Concrete, Any>> {
        return mapOf(
                "intArg" to object : IntegerArgument<Concrete>() {
                    override val isRequired = true
                    override val documentation = "An integer argument"
                    override fun Concrete.load(value: Int) {
                        this.intArg = value
                    }
                },
                "doubleArg" to object : DoubleArgument<Concrete>() {
                    override val isRequired = false
                    override val documentation = "A double argument"
                    override fun Concrete.load(value: Double) {
                        this.doubleArg = value
                    }
                }
        )
    }
}


fun main() {
    val instance = Concrete()
    val args = mapOf("intArg" to "5", "doubleArg" to "3.14")

    val argDefinitions = instance.getArgs()

    for ((argName, argDef) in argDefinitions) {
        if (argDef.isRequired && !args.containsKey(argName)) {
            throw IllegalArgumentException("Missing required argument: $argName")
        }
        argDef.validateAndLoad(instance, args[argName] ?: "")
    }

    println("intArg: ${instance.intArg}, doubleArg: ${instance.doubleArg}")
}
