import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LinearRandomIntegerValueStrategyTest {

    private lateinit var strategy: LinearRandomIntegerValueStrategy
    private lateinit var graphState: GraphState // Assume GraphState is a class you have defined

    @BeforeEach
    fun setup() {
        strategy = LinearRandomIntegerValueStrategy()
        graphState = GraphState() // Initialize your GraphState object here
    }

    @Test
    fun `test produce`() {
        // Set min and max
        strategy.minValue = 1
        strategy.maxValue = 10

        strategy.produce(graphState)

        // Assume `targetEntity` and `targetColumn` are set in your strategy
        val producedValue = graphState[strategy.targetEntity, strategy.targetColumn]

        // Check if the produced value is within the min and max range
        assertEquals(true, producedValue in 1..10)
    }
}
