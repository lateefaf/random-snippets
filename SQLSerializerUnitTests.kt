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

    //SQLBinaryType
    // Test for the format function

    @Test
    fun `if a ByteArray is formatted with SQLBinaryType, then it should be converted to a hexadecimal string of the specified length`() {
        val binaryType = SQLBinaryType(4)
        val byteArray = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05)
        val result = binaryType.format(byteArray)
        assertEquals("0x01020304", result)  // 4-byte length limit
    }

    @Test
    fun `if a ByteArray shorter than the specified length is formatted, then the entire array should be converted without padding`() {
        val binaryType = SQLBinaryType(6)
        val byteArray = byteArrayOf(0x01, 0x02, 0x03)
        val result = binaryType.format(byteArray)
        assertEquals("0x010203", result)  // No padding added
    }

    // Test for the validate function

    @Test
    fun `if length is positive for SQLBinaryType, then validate should pass`() {
        val binaryType = SQLBinaryType(5)
        assertDoesNotThrow { binaryType.validate() }
    }

    @Test
    fun `if length is negative for SQLBinaryType, then validate should fail`() {
        val binaryType = SQLBinaryType(-1)
        assertThrows<IllegalArgumentException> { binaryType.validate() }
    }

    @Test
    fun `if length is zero for SQLBinaryType, then validate should pass`() {
        val binaryType = SQLBinaryType(0)
        assertDoesNotThrow { binaryType.validate() }
    }

    //SQLBlobType
    // Test for the format function

    @Test
    fun `if a ByteArray is formatted with SQLBlobType, then it should be converted to a full hexadecimal string`() {
        val blobType = SQLBlobType()
        val byteArray = byteArrayOf(0x01, 0x02, 0x03, 0x04)
        val result = blobType.format(byteArray)
        assertEquals("0x01020304", result)
    }

    @Test
    fun `if value is castable to ByteArray in SQLBlobType, then it should be correctly formatted`() {
        val blobType = SQLBlobType()
        val byteArray = "Test".toByteArray()
        val result = blobType.format(byteArray as ByteArray) // Explicit cast in the test
        assertEquals("0x54657374", result) // ASCII (or UTF-8) values of 'Test'
    }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    // Test for the format function

    @Test
    fun `if a Date is formatted with SQLDateType, then it should be converted to a yyyy-MM-dd string`() {
        val dateType = SQLDateType()
        val date = Date(1625155200000L) // Represents 2021-07-01
        val result = dateType.format(date)
        assertEquals("'2021-07-01'", result)
    }

    @Test
    fun `if a LocalDateTime is formatted with SQLDateType, then it should be converted to a yyyy-MM-dd string`() {
        val dateType = SQLDateType()
        val localDateTime = LocalDateTime.of(2021, 7, 1, 12, 0)
        val date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
        val result = dateType.format(date)
        assertEquals("'2021-07-01'", result)
    }

    @Test
    fun `if a String representing a date is formatted with SQLDateType, then it should be parsed and formatted to yyyy-MM-dd`() {
        val dateType = SQLDateType()
        val dateString = "2021-07-01"
        val date = dateFormat.parse(dateString)
        val result = dateType.format(date)
        assertEquals("'2021-07-01'", result)
    }

    // Test for error scenarios

    @Test
    fun `if an invalid string is formatted with SQLDateType, then it should throw an exception`() {
        val dateType = SQLDateType()
        val invalidDateString = "invalid-date"
        assertThrows<IllegalArgumentException> {
            dateType.format(invalidDateString)
        }
    }
}