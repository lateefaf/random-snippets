import java.io.BufferedReader
import java.io.FileReader
import java.lang.IllegalArgumentException
import kotlin.random.Random

// WeightedMappedValueStrategy class inherits from AbstractSingleColumnStrategy and implements IArgsLoadable
class WeightedMappedValueStrategy(
        private val filePath: String
) : AbstractSingleColumnStrategy(), IArgsLoadable {

    // A map to hold the weighted probabilities for each key
    private val weightedMap: MutableMap<String, List<Pair<String, Double>>> = mutableMapOf()

    // Initialization block to preprocess the file and calculate probabilities
    init {
        // Temporary map to hold the key, value, and weight from the file
        val tempMap: MutableMap<String, MutableList<Pair<String, Int>>> = mutableMapOf()

        // Read the file and populate tempMap
        BufferedReader(FileReader(filePath)).use { reader ->
            reader.lines().forEach { line ->
                // Split the line into key, value, and weight
                val (key, value, weightStr) = line.split(",").map { it.trim() }
                val weight = weightStr.toInt()

                // Check if the weight is valid
                if (weight < 1) {
                    throw IllegalArgumentException("Weight must be an integer greater than or equal to 1")
                }

                // Add to the temporary map
                tempMap.getOrPut(key) { mutableListOf() }.add(Pair(value, weight))
            }
        }

        // Calculate probabilities and populate weightedMap
        for ((key, weightedValues) in tempMap) {
            // Calculate the total weight for each key
            val totalWeight = weightedValues.sumBy { it.second }

            // Calculate the probability for each value and store it in weightedMap
            val probabilityList = weightedValues.map { Pair(it.first, it.second.toDouble() / totalWeight) }
            weightedMap[key] = probabilityList
        }
    }

    // Method to validate arguments, if needed
    override fun validateArguments(args: Map<String, String>) {
        // Implement validation logic here if needed
    }

    // Method to get argument definitions, if needed
    override fun getArgumentDefinitions(): Map<String, ArgDefinition<Any>> {
        // Implement argument definitions here if needed
        return emptyMap()
    }

    // Method to produce the value based on weighted probabilities
    override fun produce(graphState: GraphState) {
        // Look up the key in graphState
        val lookupValue = graphState[this.targetEntity, this.targetColumn]?.toString() ?: return

        // Get the weighted values for the key
        val weightedValues = weightedMap[lookupValue] ?: return

        // Generate a random double between 0 and 1
        var randomValue = Random.nextDouble()

        // Loop through the weighted values to determine the outcome
        for ((value, probability) in weightedValues) {
            randomValue -= probability
            if (randomValue <= 0) {
                // Assign the selected value to the target column in graphState
                graphState[this.targetEntity, this.targetColumn] = value
                return
            }
        }
    }
}
