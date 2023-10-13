import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import java.io.File

class RandomMappedValueStrategyTest {

    lateinit var strategy: RandomMappedValueStrategy
    lateinit var mockGraphState: GraphState

    // Create a temporary file with test data
    val testFilePath = "testFile.csv"

    @BeforeEach
    fun setUp() {
        val fileContent = """
            A,1
            B,3
            C,2
        """.trimIndent()

        File(testFilePath).writeText(fileContent)

        strategy = RandomMappedValueStrategy()
        mockGraphState = mock(GraphState::class.java)

        val args: Map<String, String> = mapOf(
                RandomMappedValueStrategy.ARG_FILE_PATH to testFilePath
        )

        args.forEach { (key, value) ->
            strategy.getArgumentDefinitions()[key]?.load(value)
        }
    }

    @Test
    fun testInitialization() {
        // Validate that the cumulativeSumList is not null and is populated
        assertNotNull(strategy.cumulativeSumList)
        assertFalse(strategy.cumulativeSumList.isEmpty())
    }

    @Test
    fun testProduce() {
        val iterations = 1000
        val frequencyMap: MutableMap<String, Int> = mutableMapOf()

        for (i in 1..iterations) {
            strategy.produce(mockGraphState)
            val value = mockGraphState[strategy.targetEntity, strategy.targetColumn]
            frequencyMap[value.toString()] = frequencyMap.getOrDefault(value.toString(), 0) + 1
        }

        // Given the weights, A should be selected ~25% of the time and B ~75% of the time
        val aCount = frequencyMap["A"] ?: 0
        val bCount = frequencyMap["B"] ?: 0

        val aProb = aCount.toDouble() / iterations
        val bProb = bCount.toDouble() / iterations

        assertTrue(aProb > 0.2 && aProb < 0.3, "A was selected with a frequency of $aProb")
        assertTrue(bProb > 0.7 && bProb < 0.8, "B was selected with a frequency of $bProb")
    }

    @Test
    fun testFileNotExistValidation() {
        val args: Map<String, String> = mapOf(
                RandomMappedValueStrategy.ARG_FILE_PATH to "nonexistent.csv"
        )

        // Testing if an IllegalArgumentException is thrown for a non-existent file
        assertThrows(IllegalArgumentException::class.java) {
            args.forEach { (key, value) ->
                strategy.getArgumentDefinitions()[key]?.load(value)
            }
        }
    }

    // Additional test cases can be added to cover edge cases or specific scenarios
}
