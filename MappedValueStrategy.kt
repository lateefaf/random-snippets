class MappedValueStrategy(
        private val filePath: String
) : AbstractSingleColumnStrategy(), IArgsLoadable {

    companion object {
        const val ARG_LOOKUP_ENTITY = "lookupEntity"
        const val ARG_LOOKUP_COLUMN = "lookupColumn"
    }

    private var lookupEntity: String = ""
    private var lookupColumn: String = ""
    private val mappedValues: MutableMap<String, String> = mutableMapOf()

    init {
        BufferedReader(FileReader(filePath)).use { reader ->
            reader.lines().forEach { line ->
                val parts = line.split(",")
                mappedValues[parts[0].trim()] = parts[1].trim()
            }
        }
    }

    override fun validateArguments(args: Map<String, String>) {
        assert(args.containsKey(ARG_LOOKUP_ENTITY)) { throw IllegalArgumentException("lookupEntity argument is missing") }
        assert(args.containsKey(ARG_LOOKUP_COLUMN)) { throw IllegalArgumentException("lookupColumn argument is missing") }
    }

    override fun getArgumentDefinitions(): Map<String, ArgDefinition<Any>> {
        return mapOf(
                ARG_LOOKUP_ENTITY to object : ArgDefinition<String> {
                    override fun load(value: String) {
                        this@MappedValueStrategy.lookupEntity = value
                    }
                },
                ARG_LOOKUP_COLUMN to object : ArgDefinition<String> {
                    override fun load(value: String) {
                        this@MappedValueStrategy.lookupColumn = value
                    }
                }
        )
    }

    override fun produce(graphState: GraphState) {
        val lookupValue = graphState[lookupEntity, lookupColumn]
        val mappedValue = mappedValues[lookupValue.toString()] ?: throw IllegalArgumentException("No mapped value found for $lookupValue")
        graphState[this.targetEntity, this.targetColumn] = mappedValue
    }
}