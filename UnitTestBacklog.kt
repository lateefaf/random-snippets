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