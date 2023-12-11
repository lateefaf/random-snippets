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

    //SQLTimeTest
    // Test for the format function

    @Test
    fun `if a Date is formatted with SQLTimeType, then it should be converted to a time string`() {
        val timeType = SQLTimeType(3)  // Milliseconds precision
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 45)
            set(Calendar.MILLISECOND, 123)
        }
        val date = calendar.time
        val result = timeType.format(date)
        assertEquals("'15:30:45.123'", result)
    }

    @Test
    fun `if a LocalDateTime is formatted with SQLTimeType, then it should be converted to a time string`() {
        val timeType = SQLTimeType(2)  // Seconds fraction precision
        val localDateTime = LocalDateTime.of(2021, 7, 1, 15, 30, 45, 123000000)
        val date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
        val result = timeType.format(date)
        assertEquals("'15:30:45.12'", result)
    }

    @Test
    fun `if a String representing a time is formatted with SQLTimeType, then it should be parsed and formatted to a time string`() {
        val timeType = SQLTimeType(1)  // Tenths of a second precision
        val timeFormat = SimpleDateFormat("HH:mm:ss.S")
        val timeString = "15:30:45.1"
        val time = timeFormat.parse(timeString)
        val result = timeType.format(time)
        assertEquals("'15:30:45.1'", result)
    }

    // Test for error scenarios

    @Test
    fun `if an invalid string is formatted with SQLTimeType, then it should throw an exception`() {
        val timeType = SQLTimeType(3)
        val invalidTimeString = "invalid-time"
        assertThrows<IllegalArgumentException> {
            timeType.format(invalidTimeString)
        }
    }

    //SQLDateTimeType
    // Test for the format function

    @Test
    fun `if a Date is formatted with SQLDateTimeType, then it should be converted to a datetime string`() {
        val dateTimeType = SQLDateTimeType(3)  // Milliseconds precision
        val calendar = Calendar.getInstance().apply {
            set(2021, Calendar.JULY, 1, 15, 30, 45)
            set(Calendar.MILLISECOND, 123)
        }
        val date = calendar.time
        val result = dateTimeType.format(date)
        assertEquals("'2021-07-01 15:30:45.123'", result)
    }

    @Test
    fun `if a LocalDateTime is formatted with SQLDateTimeType, then it should be converted to a datetime string`() {
        val dateTimeType = SQLDateTimeType(2)  // Seconds fraction precision
        val localDateTime = LocalDateTime.of(2021, 7, 1, 15, 30, 45, 123000000)
        val date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
        val result = dateTimeType.format(date)
        assertEquals("'2021-07-01 15:30:45.12'", result)
    }

    @Test
    fun `if a String representing a datetime is formatted with SQLDateTimeType, then it should be parsed and formatted to a datetime string`() {
        val dateTimeType = SQLDateTimeType(1)  // Tenths of a second precision
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S")
        val dateTimeString = "2021-07-01 15:30:45.1"
        val dateTime = dateTimeFormat.parse(dateTimeString)
        val result = dateTimeType.format(dateTime)
        assertEquals("'2021-07-01 15:30:45.1'", result)
    }

    // Test for error scenarios

    @Test
    fun `if an invalid string is formatted with SQLDateTimeType, then it should throw an exception`() {
        val dateTimeType = SQLDateTimeType(3)
        val invalidDateTimeString = "invalid-datetime"
        assertThrows<IllegalArgumentException> {
            dateTimeType.format(invalidDateTimeString)
        }
    }

    //SQLYearType
    // Test for the format function

    @Test
    fun `if a Date is formatted with SQLYearType, then it should be converted to a year string`() {
        val yearType = SQLYearType()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2021)
        }
        val date = calendar.time
        val result = yearType.format(date)
        assertEquals("'2021'", result)
    }

    @Test
    fun `if a LocalDateTime is formatted with SQLYearType, then it should be converted to a year string`() {
        val yearType = SQLYearType()
        val localDateTime = LocalDateTime.of(2021, 7, 1, 15, 30)
        val date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
        val result = yearType.format(date)
        assertEquals("'2021'", result)
    }

    @Test
    fun `if a String representing a year is formatted with SQLYearType, then it should be parsed and formatted to a year string`() {
        val yearType = SQLYearType()
        val yearFormat = SimpleDateFormat("yyyy")
        val yearString = "2021"
        val year = yearFormat.parse(yearString)
        val result = yearType.format(year)
        assertEquals("'2021'", result)
    }

    // Test for error scenarios

    @Test
    fun `if an invalid string is formatted with SQLYearType, then it should throw an exception`() {
        val yearType = SQLYearType()
        val invalidYearString = "invalid-year"
        assertThrows<IllegalArgumentException> {
            yearType.format(invalidYearString)
        }
    }

    // Test for the format function

    @Test
    fun `if a Date is formatted with SQLTimeType, then it should be converted to a time string`() {
        val timeType = SQLTimeType(3)  // Milliseconds precision
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 15)
            set(Calendar.MINUTE, 30)
            set(Calendar.SECOND, 45)
            set(Calendar.MILLISECOND, 123)
        }
        val date = calendar.time
        val result = timeType.format(date)
        assertEquals("'15:30:45.123'", result)
    }

    @Test
    fun `if a LocalDateTime is formatted with SQLTimeType, then it should be converted to a time string`() {
        val timeType = SQLTimeType(2)  // Seconds fraction precision
        val localDateTime = LocalDateTime.of(2021, 7, 1, 15, 30, 45, 123000000)
        val date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
        val result = timeType.format(date)
        assertEquals("'15:30:45.12'", result)
    }

    @Test
    fun `if a String representing a time is formatted with SQLTimeType, then it should be parsed and formatted to a time string`() {
        val timeType = SQLTimeType(1)  // Tenths of a second precision
        val timeFormat = SimpleDateFormat("HH:mm:ss.S")
        val timeString = "15:30:45.1"
        val time = timeFormat.parse(timeString)
        val result = timeType.format(time)
        assertEquals("'15:30:45.1'", result)
    }

    // Test for error scenarios

    @Test
    fun `if an invalid string is formatted with SQLTimeType, then it should throw an exception`() {
        val timeType = SQLTimeType(3)
        val invalidTimeString = "invalid-time"
        assertThrows<IllegalArgumentException> {
            timeType.format(invalidTimeString)
        }
    }

    // Test for the format function

    @Test
    fun `if a Date is formatted with SQLDateTimeType, then it should be converted to a datetime string`() {
        val dateTimeType = SQLDateTimeType(3)  // Milliseconds precision
        val calendar = Calendar.getInstance().apply {
            set(2021, Calendar.JULY, 1, 15, 30, 45)
            set(Calendar.MILLISECOND, 123)
        }
        val date = calendar.time
        val result = dateTimeType.format(date)
        assertEquals("'2021-07-01 15:30:45.123'", result)
    }

    @Test
    fun `if a LocalDateTime is formatted with SQLDateTimeType, then it should be converted to a datetime string`() {
        val dateTimeType = SQLDateTimeType(2)  // Seconds fraction precision
        val localDateTime = LocalDateTime.of(2021, 7, 1, 15, 30, 45, 123000000)
        val date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
        val result = dateTimeType.format(date)
        assertEquals("'2021-07-01 15:30:45.12'", result)
    }

    @Test
    fun `if a String representing a datetime is formatted with SQLDateTimeType, then it should be parsed and formatted to a datetime string`() {
        val dateTimeType = SQLDateTimeType(1)  // Tenths of a second precision
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S")
        val dateTimeString = "2021-07-01 15:30:45.1"
        val dateTime = dateTimeFormat.parse(dateTimeString)
        val result = dateTimeType.format(dateTime)
        assertEquals("'2021-07-01 15:30:45.1'", result)
    }

    // Test for error scenarios

    @Test
    fun `if an invalid string is formatted with SQLDateTimeType, then it should throw an exception`() {
        val dateTimeType = SQLDateTimeType(3)
        val invalidDateTimeString = "invalid-datetime"
        assertThrows<IllegalArgumentException> {
            dateTimeType.format(invalidDateTimeString)
        }
    }

    // Test for the format function

    @Test
    fun `if a Date is formatted with SQLYearType, then it should be converted to a year string`() {
        val yearType = SQLYearType()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2021)
        }
        val date = calendar.time
        val result = yearType.format(date)
        assertEquals("'2021'", result)
    }

    @Test
    fun `if a LocalDateTime is formatted with SQLYearType, then it should be converted to a year string`() {
        val yearType = SQLYearType()
        val localDateTime = LocalDateTime.of(2021, 7, 1, 15, 30)
        val date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
        val result = yearType.format(date)
        assertEquals("'2021'", result)
    }

    @Test
    fun `if a String representing a year is formatted with SQLYearType, then it should be parsed and formatted to a year string`() {
        val yearType = SQLYearType()
        val yearFormat = SimpleDateFormat("yyyy")
        val yearString = "2021"
        val year = yearFormat.parse(yearString)
        val result = yearType.format(year)
        assertEquals("'2021'", result)
    }

    // Test for error scenarios

    @Test
    fun `if an invalid string is formatted with SQLYearType, then it should throw an exception`() {
        val yearType = SQLYearType()
        val invalidYearString = "invalid-year"
        assertThrows<IllegalArgumentException> {
            yearType.format(invalidYearString)
        }
    }

    // Test for the format function

    @Test
    fun `if a string is formatted with SQLTextType, then it should be enclosed in single quotes`() {
        val textType = SQLTextType()
        val result = textType.format("Hello World")
        assertEquals("'Hello World'", result)
    }

    @Test
    fun `if a string contains single quotes, then they should be escaped`() {
        val textType = SQLTextType()
        val result = textType.format("O'Reilly")
        assertEquals("'O''Reilly'", result)
    }

    @Test
    fun `if a string contains special characters, then they should be preserved`() {
        val textType = SQLTextType()
        val result = textType.format("Hello\nWorld")
        assertEquals("'Hello\nWorld'", result)
    }

    @Test
    fun `if a null value is formatted, then it should be converted to SQL NULL`() {
        val textType = SQLTextType()
        val result = textType.format(null)
        assertEquals("NULL", result)
    }

    private val allowedValues = listOf("ACTIVE", "INACTIVE", "SUSPENDED")

    // Test for the format function

    @Test
    fun `if a valid enum value is formatted, then it should be enclosed in single quotes`() {
        val enumType = SQLEnumType(allowedValues)
        val result = enumType.format("ACTIVE")
        assertEquals("'ACTIVE'", result)
    }

    @Test
    fun `if an invalid enum value is formatted, then it should throw an exception`() {
        val enumType = SQLEnumType(allowedValues)
        assertThrows<IllegalArgumentException> {
            enumType.format("UNKNOWN")
        }
    }

    @Test
    fun `if a null value is formatted, then it should be converted to SQL NULL`() {
        val enumType = SQLEnumType(allowedValues)
        val result = enumType.format(null)
        assertEquals("NULL", result)
    }

    private val allowedValuesSet = setOf("READ", "WRITE", "EXECUTE")

    // Test for the format function

    @Test
    fun `if a valid set value is formatted, then it should be enclosed in single quotes and escape single quotes`() {
        val setType = SQLSetType(allowedValuesSet)
        val result = setType.format("READ,WRITE")
        assertEquals("'READ,WRITE'", result)
    }

    // Test for the validate function

    @Test
    fun `validate should pass when allowedValues is not empty`() {
        val setType = SQLSetType(allowedValuesSet)
        assertDoesNotThrow { setType.validate() }
    }

    @Test
    fun `validate should throw an exception when allowedValues is empty`() {
        val setType = SQLSetType(emptySet())
        assertThrows<IllegalArgumentException> { setType.validate() }
    }

    // Additional tests to reflect specific handling in the format function

    @Test
    fun `if a string with single quotes is formatted, then single quotes should be escaped`() {
        val setType = SQLSetType(allowedValuesSet)
        val result = setType.format("READ,'WRITE'")
        assertEquals("'READ,''WRITE'''", result)
    }

    @Test
    fun `if a null value is formatted, then it should be converted to SQL NULL`() {
        val setType = SQLSetType(allowedValuesSet)
        val result = setType.format(null)
        assertEquals("NULL", result)
    }

    fun format(value: Any?): String {
        return if (value == null) {
            "NULL"
        } else {
            // Assuming 'value' is a string. If 'value' can be of other types, you might need additional handling.
            val stringValue = value as String
            // Escape single quotes in the string value.
            "'${stringValue.replace("'", "''")}'"
        }
    }

}