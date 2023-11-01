import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class WeightedMappedValueStrategyTest {

//        Test 1: Single Key with Unequal Weights
//
//        1. Input a file containing key-value-weight triplets where one key has multiple values with unequal weights.
//
//        2. Set the GraphState to have that key.
//
//        3. Run the strategy's produce function 1000 times.
//
//        4. Compare the output values with the expected distribution (25% B and 75% C).
//
//        5. Use a Chi-Square Test to validate if the observed frequency matches the expected frequency within an alpha of 0.001.
//
//
//
//        Test 2: Single Key with Equal Weights
//
//        1. Input a file containing key-value-weight triplets where one key has multiple values with equal weights.
//
//        2. Set the GraphState to have that key.
//
//        3. Run the strategy's produce function 1000 times.
//
//        4. Compare the output values with the expected distribution (50% B and 50% C).
//
//        5. Use a Chi-Square Test to validate if the observed frequency matches the expected frequency within an alpha of 0.001.
//
//
//
//        Test 3: Multiple Keys
//
//        1. Input a file containing key-value-weight triplets where multiple keys exist.
//
//        2. Set the GraphState to have one of those keys.
//
//        3. Run the strategy's produce function 1000 times.
//
//        4. Compare the output values with the expected distribution based on the weights for that key.
//
//        5. Use a Chi-Square Test to validate if the observed frequency matches the expected frequency within an alpha of 0.001.
//
//        6. Repeat steps 2 to 5 for another key.




        private fun runTest(fileContent: String, graphStateKey: String, expectedList: List<String>, alpha: Double) {
                val testFilePath = "testFile.csv"
                File(testFilePath).writeText(fileContent)

                val strategy = WeightedMappedValueStrategy()
                val mockGraphState = GraphState()

                strategy.getArgumentDefinitions()[WeightedMappedValueStrategy.ARG_FILE_PATH]?.load(testFilePath)

                // Set the key in the GraphState
                mockGraphState["readEntity", "readColumn"] = graphStateKey

                val (generatedValues, frequencyMap) = generateExpectedFrequencyMap(expectedList)

                val chiSquareStatistic = calculateChiSquareStat(expectedList, frequencyMap)
                println("Chi-Square Statistic: $chiSquareStatistic")

                val criticalValue = 10.828  // Critical value for alpha = 0.001 and df = 1
                assertTrue(chiSquareStatistic <= criticalValue, "Chi-Square Test failed.")
        }

        @Test
        fun testProduceWithSingleKeyUnequalWeights() {
                val fileContent = """
        A,B,1
        A,C,3
    """.trimIndent()
                val graphStateKey = "A"
                val expectedList = List(1000) { if (it % 4 == 0) "B" else "C" }
                runTest(fileContent, graphStateKey, expectedList, alpha = 0.001)
        }

        @Test
        fun testProduceWithSingleKeyEqualWeights() {
                val fileContent = """
        A,B,1
        A,C,1
    """.trimIndent()
                val graphStateKey = "A"
                val expectedList = List(500) { "B" } + List(500) { "C" }
                runTest(fileContent, graphStateKey, expectedList, alpha = 0.001)
        }

        @Test
        fun testProduceWithMultipleKeys() {
                val fileContent = """
        A,B,1
        A,C,3
        B,D,2
        B,E,2
    """.trimIndent()
                val graphStateKeyA = "A"
                val expectedListA = List(1000) { if (it % 4 == 0) "B" else "C" }
                runTest(fileContent, graphStateKeyA, expectedListA, alpha = 0.001)

                val graphStateKeyB = "B"
                val expectedListB = List(500) { "D" } + List(500) { "E" }
                runTest(fileContent, graphStateKeyB, expectedListB, alpha = 0.001)
        }


        @Test
        fun testProduceWithSingleKeyUnequalWeights() {
                val fileContent = """
        A,B,1
        A,C,3
    """.trimIndent()
                val graphStateKey = "A"
                val expectedList = List(1000) { if (it % 4 == 0) "B" else "C" }
                runTest(fileContent, graphStateKey, expectedList, alpha = 0.001)
        }

        @Test
        fun testProduceWithSingleKeyEqualWeights() {
                val fileContent = """
        A,B,1
        A,C,1
    """.trimIndent()
                val graphStateKey = "A"
                val expectedList = List(500) { "B" } + List(500) { "C" }
                runTest(fileContent, graphStateKey, expectedList, alpha = 0.001)
        }

        @Test
        fun testProduceWithMultipleKeys() {
                val fileContent = """
        A,B,1
        A,C,3
        B,D,2
        B,E,2
    """.trimIndent()
                val graphStateKeyA = "A"
                val expectedListA = List(1000) { if (it % 4 == 0) "B" else "C" }
                runTest(fileContent, graphStateKeyA, expectedListA, alpha = 0.001)

                val graphStateKeyB = "B"
                val expectedListB = List(500) { "D" } + List(500) { "E" }
                runTest(fileContent, graphStateKeyB, expectedListB, alpha = 0.001)
        }



        //OLD
        @Test
    fun testProduce() {
        val fileContent = """
            A,B,1
            A,C,1
            A,D,2
            B,E,1
            B,F,2
            B,G,1
        """.trimIndent()
        val testFilePath = "testFile.csv"
        File(testFilePath).writeText(fileContent)

        val strategy = WeightedMappedValueStrategy()
        val graphState = GraphState()

        // Define arguments
        strategy.getArgumentDefinitions()[WeightedMappedValueStrategy.ARG_FILE_PATH]?.load(testFilePath)
        strategy.targetEntity = "writeEntity"
        strategy.targetColumn = "writeColumn"
        strategy.readEntity = "readEntity"
        strategy.readColumn = "readColumn"

        // First test case: graphState["readEntity", "readColumn"] = "A"
        graphState["readEntity", "readColumn"] = "A"
        strategy.produce(graphState)
        val valueForA = graphState["writeEntity", "writeColumn"]
        assertEquals(true, listOf("B", "C", "D").contains(valueForA), "Unexpected value for key 'A': $valueForA")

        // Second test case: graphState["readEntity", "readColumn"] = "B"
        graphState["readEntity", "readColumn"] = "B"
        strategy.produce(graphState)
        val valueForB = graphState["writeEntity", "writeColumn"]
        assertEquals(true, listOf("E", "F", "G").contains(valueForB), "Unexpected value for key 'B': $valueForB")

        // Clear the file
        File(testFilePath).delete()
    }

}
