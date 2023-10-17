import org.junit.jupiter.api.Test
import java.io.File
import kotlin.random.Random
import kotlin.test.assertTrue

class WeightedMappedValueStrategyTest {

    @Test
    fun testProduce() {
        // Test with single key, unequal weights (A: B:1, C:3)
        runTest(
                fileContent = """
                A,B,1
                A,C,3
            """.trimIndent(),
                lookupValue = "A",
                expectedProbabilities = mapOf("B" to 0.25, "C" to 0.75),
                tolerance = 0.05
        )

        // Test with single key, equal weights (B: D:1, E:1)
        runTest(
                fileContent = """
                B,D,1
                B,E,1
            """.trimIndent(),
                lookupValue = "B",
                expectedProbabilities = mapOf("D" to 0.5, "E" to 0.5),
                tolerance = 0.05
        )

        // Test with single key, various weights (C: F:1, G:2, H:2)
        runTest(
                fileContent = """
                C,F,1
                C,G,2
                C,H,2
            """.trimIndent(),
                lookupValue = "C",
                expectedProbabilities = mapOf("F" to 0.2, "G" to 0.4, "H" to 0.4),
                tolerance = 0.05
        )

        // Test with incorrect weights (should throw an exception)
        try {
            runTest(
                    fileContent = """
                    A,D,0
                    A,E,-1
                """.trimIndent(),
                    lookupValue = "A",
                    expectedProbabilities = mapOf(),
                    tolerance = 0.05
            )
            assertTrue(false, "Should have thrown an IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // Test passed
        }
    }

    private fun runTest(fileContent: String, lookupValue: String, expectedProbabilities: Map<String, Double>, tolerance: Double) {
        val testFilePath = "testFile.csv"
        File(testFilePath).writeText(fileContent)

        val strategy = WeightedMappedValueStrategy()
        val mockGraphState = GraphState()

        strategy.getArgumentDefinitions()[WeightedMappedValueStrategy.ARG_FILE_PATH]?.load(testFilePath)

        val iterations = 10000
        val frequencyMap: MutableMap<String, Int> = mutableMapOf()

        for (i in 1..iterations) {
            mockGraphState[strategy.targetEntity, strategy.targetColumn] = lookupValue
            strategy.random = Random.Default
            strategy.produce(mockGraphState)
            val value = mockGraphState[strategy.targetEntity, strategy.targetColumn]?.toString()
            frequencyMap[value.toString()] = frequencyMap.getOrDefault(value.toString(), 0) + 1
        }

        for ((value, expectedProb) in expectedProbabilities) {
            val count = frequencyMap[value] ?: 0
            val actualProb = count.toDouble() / iterations
            assertTrue(
                    actualProb >= expectedProb - tolerance && actualProb <= expectedProb + tolerance,
                    "$value was selected with a frequency of $actualProb but expected around $expectedProb"
            )
        }
    }
}
