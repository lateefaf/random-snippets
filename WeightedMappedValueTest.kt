import java.io.File
import java.io.FileWriter

fun main() {
    // Initialize the WeightedMappedValueStrategy object with a test file path
    val strategy = WeightedMappedValueStrategy("weights.txt")

    // Initialize a GraphState object and set targetEntity and targetColumn for demonstration
    val graphState = GraphState()
    strategy.targetEntity = "TestEntity"
    strategy.targetColumn = "TestColumn"

    // A map to store the frequency of each generated value
    val frequencyMap: MutableMap<String, Int> = mutableMapOf()

    // Generate values 10000 times
    for (i in 1..10000) {
        // For demonstration, we set the targetColumn to 'A' and 'B' alternately to test the probabilities
        graphState[strategy.targetEntity, strategy.targetColumn] = if (i % 2 == 0) "A" else "B"

        // Generate the new value based on the probabilities
        strategy.produce(graphState)

        // Record the frequency of each generated value
        val generatedValue = graphState[strategy.targetEntity, strategy.targetColumn].toString()
        frequencyMap[generatedValue] = frequencyMap.getOrDefault(generatedValue, 0) + 1
    }

    // Write the frequencies to a CSV file
    FileWriter("frequency.csv").use { writer ->
        writer.append("Value,Frequency\n")
        for ((value, frequency) in frequencyMap) {
            writer.append("$value,$frequency\n")
        }
    }

    println("Frequency of each generated value has been written to frequency.csv")
}
