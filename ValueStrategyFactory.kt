class ValueStrategyFactory(private val parser: ArgumentParser, private val applicationContext: ApplicationContext) {
    fun produce(columnDescriptor: SchemaColumn): IGenerationStrategy {
        // Parse the arguments using your ArgumentParser
        val args: Map<String, String> = parser.parse(columnDescriptor.valueStrategy.args)

        // Retrieve an instance of a class that should implement IArgsLoadable
        val temp = applicationContext.getBean<Any>(columnDescriptor.valueStrategy.strategyName)  // Assume strategyName provides the bean name

        return if (temp is IArgsLoadable<*>) {
            // Cast the retrieved bean to its actual type (the concrete type that implements IArgsLoadable)
            val instance = temp as IArgsLoadable<Any>

            // Fetch the argument definitions (ArgWrapper instances) from the IArgsLoadable instance
            val argDefinitions = instance.getArgs()

            for ((argName, argWrapper) in argDefinitions) {
                if (argWrapper.isRequired && !args.containsKey(argName)) {
                    throw IllegalArgumentException("Missing required argument: $argName")
                }

                // Convert and validate the argument, then load it
                val argValue = argWrapper.convert(args[argName] ?: "")
                argWrapper.validate(instance, argValue)
                argWrapper.load(instance, argValue)
            }

            instance as IGenerationStrategy  // Assume the class also implements IGenerationStrategy
        } else {
            throw IllegalArgumentException("The specified strategy does not implement IArgsLoadable")
        }
    }
}
