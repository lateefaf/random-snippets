import kotlin.random.Random

class LinearRandomLocalDateTimeStrategyTest {

    private lateinit var mockRandom: Random
    private lateinit var mockFaker: Faker
    private lateinit var mockAppContext: ApplicationContext

    @BeforeEach
    fun setUp() {
        mockRandom = mock()
        mockFaker = mock()
        mockAppContext = mock()
        whenever(mockAppContext.getBean(Random::class.java)).thenReturn(mockRandom)
        whenever(mockAppContext.getBean(Faker::class.java)).thenReturn(mockFaker)
    }

    @Test(expected = InvalidBoundsException::class)
    fun `if min is after max then throw InvalidBoundsException`() {
        val strategy = LinearRandomLocalDateTimeStrategy(
            min = LocalDateTime.now(),
            max = LocalDateTime.now().minusYears(1)
        )
        val schema = mock(SchemaEntities::class.java)
        strategy.validateArguments(schema)
    }

    @Test(expected = RangeTooLargeException::class)
    fun `if range is more than 291 years then throw RangeTooLargeException`() {
        val strategy = LinearRandomLocalDateTimeStrategy(
            min = LocalDateTime.now().minusYears(292),
            max = LocalDateTime.now()
        )
        val schema = mock(SchemaEntities::class.java)
        strategy.validateArguments(schema)
    }

    @Test
    fun `if valid arguments are provided then no exception is thrown`() {
        val strategy = LinearRandomLocalDateTimeStrategy()
        val schema = mock(SchemaEntities::class.java)
        strategy.validateArguments(schema)
    }

    @Test
    fun `if producing date time then result is within range`() {
        val min = LocalDateTime.now().minusYears(1)
        val max = LocalDateTime.now()
        val strategy = LinearRandomLocalDateTimeStrategy(min, max)
        strategy.onConstructOrLoad(mockAppContext)

        // Mock the random number generation
        val mockedNanoSeconds = 100000000L // example nanosecond value
        whenever(mockRandom.nextLong(anyLong())).thenReturn(mockedNanoSeconds)

        val result = strategy.produce(mock(IGraphState::class.java))
        assertTrue(result.isAfter(min) && result.isBefore(max))
    }
}

Ensure that the class correctly handles edge cases, such as invalid argument combinations.
Validate that the date-time production logic operates within the expected constraints.
Confirm that exceptions are thrown as expected under erroneous conditions.

Required Tests:

Argument Validation Tests
if min is after max then throw InvalidBoundsException
if range is more than 291 years then throw RangeTooLargeException
if valid arguments are provided then no exception is thrown

Production Tests
if producing date time then result is within range
if nanoRange is set then nanoRange is not zero

All tests should pass with the current implementation of LinearRandomLocalDateTimeStrategy.
If any tests fail, raise issues for the necessary fixes before proceeding.


class FullNameFourColumnTest {

    private lateinit var mockFaker: Faker
    private lateinit var fullNameFourColumn: FullNameFourColumn
    private lateinit var mockGraphState: IGraphState

    @BeforeEach
    fun setUp() {
        mockFaker = mock()
        whenever(mockAppContext.getBean(Random::class.java)).thenReturn(mockFaker)
        mockGraphState = mock()
    }

    @Test
    fun `produce should generate valid full name`() {
        val firstName = "John"
        val middleName = "H"
        val lastName = "Doe"
        whenever(mockFaker.name().firstName()).thenReturn(firstName)
        whenever(mockFaker.name().middleName()).thenReturn(middleName)
        whenever(mockFaker.name().lastName()).thenReturn(lastName)

        val result = fullNameFourColumn.produce(mockGraphState)

        assertEquals(firstName, result[FullNameFourColumn.ARG_NAME_FIRST_NAME_COLUMN])
        assertEquals(middleName, result[FullNameFourColumn.ARG_NAME_MIDDLE_NAME_COLUMN])
        assertEquals(lastName, result[FullNameFourColumn.ARG_NAME_LAST_NAME_COLUMN])
        assertEquals("$firstName $middleName $lastName", result[FullNameFourColumn.ARG_NAME_FULL_NAME_COLUMN])
    }

    @Test
    fun `produce should generate valid first name`() {
        val firstName = "John"
        whenever(mockFaker.name().firstName()).thenReturn(firstName)

        val result = fullNameFourColumn.produce(mockGraphState)

        assertEquals(firstName, result[FullNameFourColumn.ARG_NAME_FIRST_NAME_COLUMN])
    }

    @Test
    fun `produce should generate valid middle name`() {
        val middleName = "A"
        whenever(mockFaker.name().middleName()).thenReturn(middleName)

        val result = fullNameFourColumn.produce(mockGraphState)

        assertEquals(middleName, result[FullNameFourColumn.ARG_NAME_MIDDLE_NAME_COLUMN])
    }

    @Test
    fun `produce should generate valid last name`() {
        val lastName = "Doe"
        whenever(mockFaker.name().lastName()).thenReturn(lastName)

        val result = fullNameFourColumn.produce(mockGraphState)

        assertEquals(lastName, result[FullNameFourColumn.ARG_NAME_LAST_NAME_COLUMN])
    }

    @Test
    fun `produce should generate valid full name`() {
        val firstName = "John"
        val middleName = "H"
        val lastName = "Doe"
        whenever(mockFaker.name().firstName()).thenReturn(firstName)
        whenever(mockFaker.name().middleName()).thenReturn(middleName)
        whenever(mockFaker.name().lastName()).thenReturn(lastName)

        val result = fullNameFourColumn.produce(mockGraphState)

        assertEquals("$firstName $middleName $lastName", result[FullNameFourColumn.ARG_NAME_FULL_NAME_COLUMN])
    }




    @Test
    fun `getVirtualColumns should return correct set of columns`() {
        val virtualColumns = fullNameFourColumn.getVirtualColumns()
        assertTrue(
            virtualColumns.containsAll(
                setOf(
                    FullNameFourColumn.ARG_NAME_FIRST_NAME_COLUMN,
                    FullNameFourColumn.ARG_NAME_MIDDLE_NAME_COLUMN,
                    FullNameFourColumn.ARG_NAME_LAST_NAME_COLUMN,
                    FullNameFourColumn.ARG_NAME_FULL_NAME_COLUMN
                )
            )
        )
    }
}
Test the produce method to ensure it correctly generates and assigns names to the respective columns.
Validate that the getVirtualColumns method returns the correct set of column names.
Ensure the class behaves as expected under normal conditions.Needtesting.kt



fun `validateArguments should throw InvalidArgumentException for non-positive sigma`() {
    val args = mapOf("sigma" to "0")
    normalDistributionStrategy.sigma = args["sigma"]?.toDouble()

    normalDistributionStrategy.validateArguments(args, mockSchemaEntities)
}

fun `produce should return a value based on normal distribution`() {
    normalDistributionStrategy.mu = 0.0
    normalDistributionStrategy.sigma = 1.0

    mockStatic(Random::class.java)
    whenever(Random.nextDouble(1.0)).thenReturn(0.5, 0.5) // Mock two calls

    val result = normalDistributionStrategy.produce()
    assertNotNull(result)
}

@Test
fun `chi-squared test for normal distribution`() {
    normalDistributionStrategy.mu = 0.0
    normalDistributionStrategy.sigma = 1.0

    val values = mutableListOf<Double>()
    val numberOfValues = 1000 // Adjust this number based on your requirements

    for (i in 0 until numberOfValues) {
        updateMockRandomForCurrentStep(i, numberOfValues)
        values.add(normalDistributionStrategy.produce())
    }

    // Now, values contains a list of normally distributed values.
    // Next, conduct the chi-squared test on these values.
    val chiSquaredResult = performChiSquaredTest(values)

    // Check if the chi-squared result is within an acceptable range
    // to confirm a normal distribution.
    // This will depend on your chi-squared test implementation.
}

fun performChiSquaredTest(values: List<Double>, numberOfBins: Int, mu: Double, sigma: Double): Double {
    // Define the bins
    val max = values.maxOrNull() ?: mu + 3 * sigma
    val min = values.minOrNull() ?: mu - 3 * sigma
    val binWidth = (max - min) / numberOfBins
    val bins = IntArray(numberOfBins)

    // Calculate observed frequencies
    values.forEach { value ->
        val binIndex = ((value - min) / binWidth).toInt().coerceIn(0, numberOfBins - 1)
        bins[binIndex]++
    }

    // Calculate expected frequencies and apply chi-squared test
    var chiSquared = 0.0
    for (i in 0 until numberOfBins) {
        val binMidPoint = min + i * binWidth + binWidth / 2
        val expectedFreq = expectedFrequency(binMidPoint, binWidth, mu, sigma, values.size)
        chiSquared += ((bins[i] - expectedFreq).pow(2)) / expectedFreq
    }

    return chiSquared
}

fun expectedFrequency(x: Double, binWidth: Double, mu: Double, sigma: Double, totalSize: Int): Double {
    val z1 = (x - mu) / sigma
    val z2 = (x + binWidth - mu) / sigma
    return (phi(z2) - phi(z1)) * totalSize
}

// Cumulative distribution function for the standard normal distribution
fun phi(x: Double): Double {
    return 0.5 * (1 + erf(x / sqrt(2.0)))
}

// Approximation of the error function
fun erf(x: Double): Double {
    // constants
    val a1 =  0.254829592
    val a2 = -0.284496736
    val a3 =  1.421413741
    val a4 = -1.453152027
    val a5 =  1.061405429
    val p  =  0.3275911

    val sign = if (x < 0) -1 else 1
    val absX = kotlin.math.abs(x)

    // A&S formula 7.1.26
    val t = 1.0 / (1.0 + p * absX)
    val y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * exp(-absX * absX)

    return sign * y
}