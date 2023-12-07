class SQLSerializerUnitTests {

    //SQLStringType
    // Test for the format function
    @Test
    fun `format should keep string unchanged if under max length`() {
        val stringType = SQLStringType(10)
        val result = stringType.format("Test")
        assertEquals("'Test'", result)
    }

    @Test
    fun `format should truncate string if over max length`() {
        val stringType = SQLStringType(4)
        val result = stringType.format("Testing")
        assertEquals("'Test'", result)
    }

    @Test
    fun `format should escape single quotes`() {
        val stringType = SQLStringType(20)
        val result = stringType.format("O'Reilly")
        assertEquals("'O''Reilly'", result)
    }

    // Test for the validate function

    @Test
    fun `validate should pass for positive length`() {
        val stringType = SQLStringType(5)
        assertDoesNotThrow { stringType.validate() }
    }

    @Test
    fun `validate should fail for negative length`() {
        val stringType = SQLStringType(-1)
        assertThrows<IllegalArgumentException> { stringType.validate() }
    }

    @Test
    fun `validate should pass for zero length`() {
        val stringType = SQLStringType(0)
        assertDoesNotThrow { stringType.validate() }
    }

    //SQLDecimalType
    // Test for the format function

    @Test
    fun `format should adhere to specified scale`() {
        val decimalType = SQLDecimalType(10, 2)
        val result = decimalType.format(BigDecimal("123.456"))
        assertEquals("123.46", result)
    }

    @Test
    fun `format should round down when necessary`() {
        val decimalType = SQLDecimalType(10, 2)
        val result = decimalType.format(BigDecimal("123.454"))
        assertEquals("123.45", result)
    }

    @Test
    fun `format should round up when necessary`() {
        val decimalType = SQLDecimalType(10, 2)
        val result = decimalType.format(BigDecimal("123.455"))
        assertEquals("123.46", result)
    }

    // Test for the validate function

    @Test
    fun `validate should pass for positive precision and scale`() {
        val decimalType = SQLDecimalType(10, 2)
        assertDoesNotThrow { decimalType.validate() }
    }

    @Test
    fun `validate should fail for negative precision`() {
        val decimalType = SQLDecimalType(-1, 2)
        assertThrows<IllegalArgumentException> { decimalType.validate() }
    }

    @Test
    fun `validate should fail for negative scale`() {
        val decimalType = SQLDecimalType(10, -1)
        assertThrows<IllegalArgumentException> { decimalType.validate() }
    }

    @Test
    fun `validate should fail if scale is greater than precision`() {
        val decimalType = SQLDecimalType(2, 3)
        assertThrows<IllegalArgumentException> { decimalType.validate() }
    }

    //SQLFloatType
    // Test for the format function

    @Test
    fun `if a Float value is formatted, then it should be rounded and truncated according to the specified scale and precision`() {
        val floatType = SQLFloatType(5, 2)
        val result = floatType.format(123.4567f)
        assertEquals("123.46", result)
    }

    @Test
    fun `if a Float value exceeds the specified precision, then it should be truncated to match the precision`() {
        val floatType = SQLFloatType(4, 1)
        val result = floatType.format(1234.567f)
        assertEquals("123.5", result) // Assuming it truncates after processing precision and scale
    }

    // Test for the validate function

    @Test
    fun `if precision and scale are positive for SQLFloatType, then validate should pass`() {
        val floatType = SQLFloatType(5, 2)
        assertDoesNotThrow { floatType.validate() }
    }

    @Test
    fun `if precision is negative for SQLFloatType, then validate should fail`() {
        val floatType = SQLFloatType(-1, 2)
        assertThrows<IllegalArgumentException> { floatType.validate() }
    }

    @Test
    fun `if scale is negative for SQLFloatType, then validate should fail`() {
        val floatType = SQLFloatType(5, -1)
        assertThrows<IllegalArgumentException> { floatType.validate() }
    }

    @Test
    fun `if scale is greater than precision for SQLFloatType, then validate should fail`() {
        val floatType = SQLFloatType(2, 3)
        assertThrows<IllegalArgumentException> { floatType.validate() }
    }
}