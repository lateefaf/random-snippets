import kotlin.math.sqrt
import kotlin.math.ln
import kotlin.math.cos
import kotlin.math.PI
import kotlin.random.Random

class NormalDistrib {

    companion object {
        const val ARG_MIN = "min"
        const val ARG_MAX = "max"
        const val ARG_MEAN = "mean"
        const val ARG_STANDARD_DEVIATION = "std_dev"
    }

    var min: Double? = null
    var max: Double? = null
    var mean: Double? = null
    var stdDev: Double? = null

    fun validateArguments(args: Map<String, String>, schema: SchemaEntities) {
        val hasMin = args.containsKey(ARG_MIN)
        val hasMax = args.containsKey(ARG_MAX)

        if (hasMin && !hasMax || !hasMin && hasMax) {
            throw IllegalArgumentException("Both min and max should exist or none should exist.")
        }

        if (hasMin && hasMax) {
            if (min!! > max!!) {
                throw IllegalArgumentException("Min should be less than Max.")
            }
        }
    }

    fun getArgumentDefinitions(): Map<String, ArgDefinition<Any>> {
        return mapOf(
                ARG_MIN to object : DoubleArgument {
                    override fun required(): Boolean {
                        return false
                    }

                    override fun load(value: Double) {
                        this@NormalDistributionStrategy.min = value
                    }
                },
                ARG_MAX to object : DoubleArgument {
                    override fun required(): Boolean {
                        return false
                    }

                    override fun load(value: Double) {
                        this@NormalDistributionStrategy.max = value
                    }
                },
                ARG_MEAN to object : DoubleArgument {
                    override fun required(): Boolean {
                        return false
                    }

                    override fun load(value: Double) {
                        this@NormalDistributionStrategy.mean = value
                    }
                },
                ARG_STANDARD_DEVIATION to object : DoubleArgument {
                    override fun required(): Boolean {
                        return false
                    }

                    override fun load(value: Double) {
                        this@NormalDistributionStrategy.stdDev = value
                    }
                }
        )
    }

    fun produce(): Double {
        // Generate two uniformly distributed random numbers between 0 and 1
        val u1 = Random.nextDouble(1.0)
        val u2 = Random.nextDouble(1.0)

        // Box-Muller Transform
        val z1 = sqrt(-2.0 * ln(u1)) * cos(2.0 * PI * u2)

        val m = mean ?: 0.0
        val s = stdDev ?: 1.0
        var generatedValue = z1 * s + m

        // Manually coerce generatedValue to be within min and max
        if (min != null && generatedValue < min!!) {
            generatedValue = min!!
        }
        if (max != null && generatedValue > max!!) {
            generatedValue = max!!
        }

        return generatedValue
    }
}