import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class WeightedRandomValueStrategyTest {

    @Test
    fun testProduce() {
        // Test with two values, unequal weights (A: 1, B: 3)
        val expectedList1 = generateExpectedList(mapOf("A" to 1, "B" to 3))
        runTest(
                fileContent = """
                A,1
                B,3
            """.trimIndent(),
                expectedValues = expectedList1
        )

        // Test with two values, equal weights (A: 1, B: 1)
        val expectedList2 = generateExpectedList(mapOf("A" to 1, "B" to 1))
        runTest(
                fileContent = """
                A,1
                B,1
            """.trimIndent(),
                expectedValues = expectedList2
        )

        // Test with four values, various weights
        val expectedList3 = generateExpectedList(mapOf("A" to 1, "B" to 1, "C" to 2, "D" to 2))
        runTest(
                fileContent = """
                A,1
                B,1
                C,2
                D,2
            """.trimIndent(),
                expectedValues = expectedList3
        )
    }

    // Helper function to generate the expected list of values based on weights
    private fun generateExpectedList(weightMap: Map<String, Int>): List<String> {
        val list = mutableListOf<String>()
        for ((value, weight) in weightMap) {
            list.addAll(List(weight) { value })
        }
        return list
    }

    // Main test function
    private fun runTest(fileContent: String, expectedValues: List<String>) {
        val testFilePath = "testFile.csv"
        File(testFilePath).writeText(fileContent)

        val strategy = WeightedRandomValueStrategy()
        val mockGraphState = GraphState()

        strategy.getArgumentDefinitions()[WeightedRandomValueStrategy.ARG_FILE_PATH]?.load(testFilePath)

        val generatedValues = mutableListOf<String>()

        for (i in expectedValues.indices) {
            strategy.produce(mockGraphState)
            val value = mockGraphState[strategy.targetEntity, strategy.targetColumn]?.toString()
            generatedValues.add(value.toString())
        }

        assertEquals(expectedValues, generatedValues, "Generated values did not match the expected values.")
    }
}
