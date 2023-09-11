package test

// Define callback types
typealias ValidationCallback<T> = (T) -> Unit
typealias LoadCallback<T> = (T) -> Unit

// Refactor IArgsLoadable to use callbacks
interface IArgsLoadable {
    fun getArgs(): Map<String, ArgWrapper>
}

// Wrapper to hold validation and load callbacks along with other information
data class ArgWrapper(
        val isRequired: Boolean,
        val convert: (String) -> Any,
        val validate: ValidationCallback<Any>,
        val load: LoadCallback<Any>
)

// Your Concrete class
class Concrete : IArgsLoadable {
    var intArg: Int = 0
    var doubleArg: Double = 0.0

    override fun getArgs(): Map<String, ArgWrapper> {
        return mapOf(
                "intArg" to ArgWrapper(
                        isRequired = true,
                        convert = { it.toInt() },
                        validate = { /* You can add any extra validation logic here */ },
                        load = { this.intArg = it as Int }
                ),
                "doubleArg" to ArgWrapper(
                        isRequired = false,
                        convert = { it.toDouble() },
                        validate = { /* You can add any extra validation logic here */ },
                        load = { this.doubleArg = it as Double }
                )
        )
    }
}

// Sample main function
fun main() {
    val instance = Concrete()
    val args = mapOf("intArg" to "5", "doubleArg" to "3.14")

    val argDefinitions = instance.getArgs()

    for ((argName, argWrapper) in argDefinitions) {
        if (argWrapper.isRequired && !args.containsKey(argName)) {
            throw IllegalArgumentException("Missing required argument: $argName")
        }
        val value = argWrapper.convert(args[argName] ?: "")
        argWrapper.validate(value)
        argWrapper.load(value)
    }

    println("intArg: ${instance.intArg}, doubleArg: ${instance.doubleArg}")
}
