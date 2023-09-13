class ValueStrategyFactory(private val parser: ArgumentParser, private val applicationContext: ApplicationContext) {
    fun produce(applicationContext: ApplicationContext, columnDescriptor: SchemaColumn): IGenerationStrategy {
        val args: Map<String, String> = parser.parse(columnDescriptor.valueStrategy.args) // assumed the args are stored in a string field
        val temp = applicationContext.getBean<Any>(columnDescriptor.valueStrategy.beanName) // assumed the bean name is stored in a field

        if (temp is IArgsLoadable<*>) {
            val instance = temp as IArgsLoadable<*>
            val allArgs = instance.getAllArgs()

            for ((argName, argWrapper) in allArgs) {
                if (argWrapper.isRequired && !args.containsKey(argName)) {
                    throw IllegalArgumentException("Missing required argument: $argName")
                }

                val stringValue = args[argName] ?: ""
                val typedValue = argWrapper.convert(stringValue)

                @Suppress("UNCHECKED_CAST")
                (argWrapper as ArgWrapper<Any, Any>).validateAndLoad(instance, typedValue)
            }

            // ... your remaining logic to produce IGenerationStrategy instance goes here
            return instance as IGenerationStrategy // Just a sample, your actual logic may vary
        } else {
            throw IllegalArgumentException("Bean does not implement IArgsLoadable: ${columnDescriptor.valueStrategy.beanName}")
        }
    }
}
