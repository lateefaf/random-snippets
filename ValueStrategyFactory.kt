// ... Your imports ...

class ValueStrategyFactory {
    private val parser = ArgumentParser()

    fun produce(applicationContext: ApplicationContext, columnDescriptor: SchemaColumn): IGenerationStrategy {
        val args: Map<String, String> = parser.parse("columnDescriptor.valueStrategy.args")
        val temp = applicationContext.getBean<Any>("columnDescriptor.valueStrategy") //check for null

        if (temp is IArgsLoadable<*>) {
            val instance = temp as IArgsLoadable<Any>

            // Assuming getArgs() now includes both required and optional arguments
            val allArgs = instance.getArgs()

            for ((argName, argDefinition) in allArgs) {
                if (argDefinition is IntegerArgument<*>) {
                    // Since the value is coming from a map, ensure the key exists to prevent null issues
                    val value = args[argName] ?: throw IllegalArgumentException("Missing required argument: $argName")
                    argDefinition.validateAndLoad(instance, value)
                }
            }

            // Validate and load remaining args
            for ((argName, value) in args) {
                allArgs[argName]?.validateAndLoad(instance, value)
            }

            return instance as IGenerationStrategy
        } else {
            throw IllegalArgumentException("Bean is not an instance of IArgsLoadable.")
        }
    }
}
