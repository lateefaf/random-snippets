import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals

class WeightedMappedValueStrategyTest {

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
