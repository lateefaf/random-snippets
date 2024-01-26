import org.junit.jupiter.api.Test
import java.io.File
import kotlin.math.pow
import kotlin.random.Random
import kotlin.test.assertTrue

class WeightedRandomValueStrategyTest {

    

    @Test
    fun testProduce() {
        val alpha = 0.05 // significance level

        // Test with two values, unequal weights (A: 1, B: 3)
        val expectedList = List(250) { "A" } + List(750) { "B" }
        runTest(
                fileContent = """
                A,1
                B,3
            """.trimIndent(),
                expectedList = expectedList,
                alpha = alpha
        )
    }

    private fun runTest(fileContent: String, expectedList: List<String>, alpha: Double) {
        val testFilePath = "testFile.csv"
        File(testFilePath).writeText(fileContent)

        val strategy = WeightedRandomValueStrategy()
        val mockGraphState = GraphState()

        strategy.getArgumentDefinitions()[WeightedRandomValueStrategy.ARG_FILE_PATH]?.load(testFilePath)

        val iterations = 1000
        val frequencyMap: MutableMap<String, Int> = mutableMapOf()

        for (i in 0 until iterations) {
            strategy.random = Random.Default
            strategy.produce(mockGraphState)
            val value = mockGraphState[strategy.targetEntity, strategy.targetColumn]?.toString()
            frequencyMap[value.toString()] = frequencyMap.getOrDefault(value.toString(), 0) + 1
        }

        // Perform a Chi-Square test for goodness-of-fit
        val expectedFrequencyMap = expectedList.groupingBy { it }.eachCount()
        var chiSquareStatistic = 0.0

        for ((value, expectedFreq) in expectedFrequencyMap) {
            val observedFreq = frequencyMap.getOrDefault(value, 0)
            val diff = observedFreq - expectedFreq
            chiSquareStatistic += (diff.toDouble().pow(2)) / expectedFreq
        }

        // Degrees of Freedom = Categories - 1
        val df = expectedFrequencyMap.keys.size - 1
        // Critical value for alpha = 0.05 and df = 1 is approximately 3.841
        val criticalValue = 3.841

        println("Chi-Square Statistic: $chiSquareStatistic")
        println("Critical Value: $criticalValue")

        assertTrue(chiSquareStatistic <= criticalValue, "Chi-Square Test failed. The observed frequencies significantly differ from the expected frequencies.")
    }
}


private fun updateMockRandomForCurrentStep(mockRandom: Random, currentStep: Int, expectedSteps: Int){
    val stepSize = 1.0/ expectedSteps
    val result = if(currentStep ==0){
        0.999
    }else{
        1.0 - stepSize * (currentStep + 1.0)
    }
    whenever(mockRandom.nextDouble()).thenReturn(result)
}