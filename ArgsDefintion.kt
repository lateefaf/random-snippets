
abstract class ArgDefinition<TSelf : IArgsLoadable<TSelf>, TValue> {
    abstract val isRequired: Boolean
    abstract val documentation: String

    open fun TSelf.validate(value: TValue) {}
    abstract fun TSelf.load(value: TValue)

    fun validateAndLoad(self: TSelf, value: String) {
        val convertedValue: TValue = convert(value)
        self.validate(convertedValue)
        self.load(convertedValue)
    }

    abstract fun convert(value: String): TValue
}

abstract class IntegerArgument<TSelf : IArgsLoadable<TSelf>> : ArgDefinition<TSelf, Int>() {
    override fun convert(value: String) = value.toInt()
}

abstract class DoubleArgument<TSelf : IArgsLoadable<TSelf>> : ArgDefinition<TSelf, Double>() {
    override fun convert(value: String) = value.toDouble()
}

abstract class StringArgument<TSelf : IArgsLoadable<TSelf>> : ArgDefinition<TSelf, String>() {
    override fun convert(value: String) = value
}