enum class PrimitiveType {
    INTEGER, DOUBLE, FLOAT, LONG
}

class LinearRandomValueStrategy(
        private val type: PrimitiveType
) : AbstractSingleColumnStrategy(), IArgsLoadable {

    companion object {
        const val ARG_MIN = "min"
        const val ARG_MAX = "max"
    }

    private var minString: String = "0"
    private var maxString: String = "0"

    override fun validateArguments(args: Map<String, String>) {
        when (type) {
            PrimitiveType.INTEGER -> {
                assert(args[ARG_MIN]!!.toInt() <= args[ARG_MAX]!!.toInt()) { throw IllegalArgumentException("min should be less than or equal to max") }
            }
            PrimitiveType.DOUBLE -> {
                assert(args[ARG_MIN]!!.toDouble() <= args[ARG_MAX]!!.toDouble()) { throw IllegalArgumentException("min should be less than or equal to max") }
            }
            // ... Handle other types here
        }
    }

    override fun getArgumentDefinitions(): Map<String, ArgDefinition<Any>> {
        return mapOf(
                ARG_MIN to object : ArgDefinition<Any> {
                    override fun load(value: Any) {
                        this@LinearRandomValueStrategy.minString = value.toString()
                    }
                },
                ARG_MAX to object : ArgDefinition<Any> {
                    override fun load(value: Any) {
                        this@LinearRandomValueStrategy.maxString = value.toString()
                    }
                }
        )
    }

    override fun produce(graphState: GraphState) {
        when (type) {
            PrimitiveType.INTEGER -> {
                graphState[this.targetEntity, this.targetColumn] = faker.number().numberBetween(minString.toInt(), maxString.toInt())
            }
            PrimitiveType.DOUBLE -> {
                graphState[this.targetEntity, this.targetColumn] = faker.number().numberBetween(minString.toDouble(), maxString.toDouble())
            }
            // ... Handle other types here
        }
    }
}
