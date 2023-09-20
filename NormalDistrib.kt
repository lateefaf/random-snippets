import kotlin.math.sqrt
import kotlin.math.ln
import kotlin.math.cos
import kotlin.math.PI
import kotlin.random.Random

fun generateNormal(mean: Double, stdDev: Double, n: Int): List<Double> {
    return List(n) {
        val u1 = Random.nextDouble()
        val u2 = Random.nextDouble()

        val z0 = sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2)
        z0 * stdDev + mean
    }
}

fun main() {
    val mean = 0.0
    val stdDev = 1.0
    val n = 10  // Number of doubles you want to generate

    val normalValues = generateNormal(mean, stdDev, n)
    println(normalValues)
}
