class SQLSerializer(
        private val tableName: String,
        private val appendMode: Boolean = false,
        private val extension: String = ".sql"
) {
    // Assumed Writer Initialization (Could be a File writer or similar)
    private val writer = StringBuilder()

    fun serialize(graphState: IGraphState, schemaEntities: SchemaEntities) {
        val schemaEntity = schemaEntities[entityName]
        val graphEntity = graphState[entityName]!!

        schemaEntities.forEach { _ ->

            val columnNames = ArrayList<String>()
            val values = ArrayList<String>()

            for(schemaIdx in 0 until graphEntity.size){
                val columnNames = ArrayList<String>(schemaEntity.size)
                val values = ArrayList<String>()

                val possibleColumns = schemaEntity[schemaIdx]

                var found = false
                for(column in possibleColumns){
                    if (graphEntity.containsKey(column.columnName)){
                        var value = this.formatColumn(column.columnName, graphEntity[column.columnName] ?: "")
                        if(column.length == -1){
                            value = value.substring(0, minOf(value.length, column.length))
                        }
                        columnNames.add(columnName)
                        values.add(value)

                        found = true
                        break
                    }
                }

                if(!found){
                    //TODO Error
                }
            }

            val columnsString = columnNames.joinToString(", ")
            val valuesString = values.joinToString(", ")

            val sqlStatement = "INSERT INTO $tableName ($columnsString) VALUES ($valuesString);"
            this.writer.write(sqlStatement)
        }

        this.writer.flush()
    }

    private fun formatColumn(schemaColumn: SchemaColumn, value: Any): String {
        // Format value based on its type and schemaColumn properties
        // This is where you'll handle type conversion, length truncation, etc.
        return when (value) {
            is String -> {
                var formattedValue = value
                if (schemaColumn.length != -1) {
                    formattedValue = formattedValue.substring(0, min(formattedValue.length, schemaColumn.length))
                }
                "'$formattedValue'"
            }
            // Handle other types like Date, Number, etc.
            else -> value.toString() // Simplified for example purposes
        }
    }

    private fun formatColumn(schemaColumn: SchemaColumn, value: Any): String {
        return when (value) {
            is String -> formatStringType(schemaColumn, value)
            "VARBINARY" -> formatVarBinary(value as ByteArray, schemaColumn.length)
            is BigDecimal -> value.setScale(schemaColumn.decimalPlaces, RoundingMode.HALF_UP).toString()
            is Float, is Double -> BigDecimal(value.toString()).setScale(schemaColumn.decimalPlaces, RoundingMode.HALF_UP).toString()
            is Date -> formatDateType(schemaColumn, value)
            is ByteArray -> formatBinaryType(schemaColumn, value)
            // ... [Other types]
            else -> value.toString()
        }
    }

    private fun formatStringType(schemaColumn: SchemaColumn, value: String): String {
        var formattedValue = value.replace("'", "''")
        when (schemaColumn.type) {
            "CHAR", "VARCHAR", "TEXT", "TINYTEXT", "MEDIUMTEXT" -> {
                if (schemaColumn.length != -1) {
                    formattedValue = formattedValue.substring(0, minOf(formattedValue.length, schemaColumn.length))
                }
            }
            "ENUM", "SET" -> {
                // Special handling for ENUM or SET if needed
            }
        }
        return "'$formattedValue'"
    }

    private fun formatDateType(schemaColumn: SchemaColumn, value: Date): String {
        val format = when (schemaColumn.type) {
            "DATE" -> SimpleDateFormat("yyyy-MM-dd")
            "DATETIME", "TIMESTAMP" -> SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            "TIME" -> SimpleDateFormat("HH:mm:ss")
            "YEAR" -> SimpleDateFormat("yyyy")
            else -> SimpleDateFormat("yyyy-MM-dd HH:mm:ss") // Default format
        }
        return "'${format.format(value)}'"
    }

    private fun formatBinaryType(schemaColumn: SchemaColumn, value: ByteArray): String {
        val formattedValue = value.take(schemaColumn.length).joinToString("") { String.format("%02X", it) }
        return "0x$formattedValue"
    }
    private fun formatVarBinary(value: ByteArray, length: Int): String {
        val formattedValue = value.take(length).joinToString("") { String.format("%02X", it) }
        return "0x$formattedValue"
    }
}

// Usage Example
val columnTypes = mapOf("columnName1" to "VARCHAR", "columnName2" to "INT")
val serializer = SQLSerializer("tableName", columnTypes)



val columnTypes = mapOf(
        "columnName1" to SQLStringType(255),
        "columnName2" to SQLFloatType(10, 2),
        "columnName3" to SQLVarBinaryType(64)
)
val serializer = SQLSerializer("tableName", columnTypes)


private fun formatColumn(columnName: String, value: Any): String {
    val dataType = columnDataTypes[columnName] ?: return value.toString()

    return when (dataType) {
        is SQLStringType -> formatString(value.toString(), dataType.length)
        is SQLIntegerType -> value.toString()
        is SQLDecimalType -> formatDecimal(value as BigDecimal, dataType.precision, dataType.scale)
        is SQLFloatType -> formatFloat(value as Float, dataType.precision, dataType.scale)
        is SQLBinaryType -> formatBinary(value as ByteArray, dataType.length)
        is SQLBlobType -> formatBlob(value as ByteArray)
        is SQLDateType -> formatDate(value as Date)
        is SQLTimeType -> formatTime(value as Date, dataType.fractionalSecondsPrecision)
        is SQLDateTimeType -> formatDateTime(value as Date, dataType.fractionalSecondsPrecision)
        is SQLTimestampType -> formatTimestamp(value as Date, dataType.fractionalSecondsPrecision)
        is SQLYearType -> formatYear(value as Date)
        is SQLTextType -> formatText(value.toString())
        is SQLEnumType, is SQLSetType -> formatEnumSet(value.toString())
        // ... additional handling for other types if necessary
        else -> value.toString()
    }
}

private fun formatString(value: String, length: Int): String {
    val truncatedValue = if (length >= 0) value.substring(0, minOf(value.length, length)) else value
    return "'${truncatedValue.replace("'", "''")}'"
}

private fun formatDecimal(value: BigDecimal, precision: Int, scale: Int): String {
    return value.setScale(scale, RoundingMode.HALF_UP).toPlainString()
}

private fun formatFloat(value: Float, precision: Int, scale: Int): String {
    return BigDecimal.valueOf(value.toDouble()).setScale(scale, RoundingMode.HALF_UP).toPlainString()
}

private fun formatBinary(value: ByteArray, length: Int): String {
    val formattedValue = value.take(length).joinToString("") { "%02x".format(it) }
    return "0x$formattedValue"
}

private fun formatBlob(value: ByteArray): String {
    val formattedValue = value.joinToString("") { "%02x".format(it) }
    return "0x$formattedValue"
}

private fun formatDate(value: Any): String {
    val date = when (value) {
        is Date -> value
        is Calendar -> value.time
        // Add more conversions if needed
        else -> throw IllegalArgumentException("Unsupported type for formatDate. Expected Date or Calendar.")
    }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    return "'${dateFormat.format(date)}'"
}

private fun formatDate(value: Any): String {
    val date = when (value) {
        is Date -> value
        is Calendar -> value.time
        else -> throw UnsupportedTypeException(value, "Date or Calendar")
    }
    val dateFormat = SimpleDateFormat("yyyy-MM-dd")
    return "'${dateFormat.format(date)}'"
}



private fun formatTime(value: Any, fsp: Int): String {
    val date = when (value) {
        is Date -> value
        is Calendar -> value.time
        else -> throw IllegalArgumentException("Unsupported type for formatTime. Expected Date or Calendar.")
    }
    val timeFormat = SimpleDateFormat("HH:mm:ss.${"S".repeat(fsp)}")
    return "'${timeFormat.format(date)}'"
}


private fun formatDateTime(value: Any, fsp: Int): String {
    val dateTime = when (value) {
        is Date -> value
        is Calendar -> value.time
        else -> throw IllegalArgumentException("Unsupported type for formatDateTime. Expected Date or Calendar.")
    }
    val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.${"S".repeat(fsp)}")
    return "'${dateTimeFormat.format(dateTime)}'"
}


private fun formatTimestamp(value: Any, fsp: Int): String {
    val timestamp = when (value) {
        is Date -> value
        is Calendar -> value.time
        else -> throw IllegalArgumentException("Unsupported type for formatTimestamp. Expected Date or Calendar.")
    }
    val timestampFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.${"S".repeat(fsp)}")
    return "'${timestampFormat.format(timestamp)}'"
}


private fun formatYear(value: Any): String {
    val year = when (value) {
        is Date -> value
        is Calendar -> value.time
        else -> throw IllegalArgumentException("Unsupported type for formatYear. Expected Date or Calendar.")
    }
    val yearFormat = SimpleDateFormat("yyyy")
    return "'${yearFormat.format(year)}'"
}


private fun formatText(value: String): String {
    return "'${value.replace("'", "''")}'"
}

private fun formatEnumSet(value: String): String {
    return "'${value.replace("'", "''")}'"
}

sealed class SQLDataType

data class SQLStringType(val length: Int) : SQLDataType()
data class SQLIntegerType : SQLDataType() // For INT, TINYINT, SMALLINT, MEDIUMINT, BIGINT
data class SQLDecimalType(val precision: Int, val scale: Int) : SQLDataType() // For DECIMAL, NUMERIC
data class SQLFloatType(val precision: Int, val scale: Int) : SQLDataType() // For FLOAT, DOUBLE
data class SQLBinaryType(val length: Int) : SQLDataType() // For BINARY, VARBINARY
data class SQLBlobType : SQLDataType() // For TINYBLOB, BLOB, MEDIUMBLOB, LONGBLOB
data class SQLDateType : SQLDataType() // For DATE
data class SQLTimeType(val fractionalSecondsPrecision: Int) : SQLDataType() // For TIME(fsp)
data class SQLDateTimeType(val fractionalSecondsPrecision: Int) : SQLDataType() // For DATETIME(fsp)
data class SQLTimestampType(val fractionalSecondsPrecision: Int) : SQLDataType() // For TIMESTAMP(fsp)
data class SQLYearType : SQLDataType() // For YEAR

data class SQLTextType : SQLDataType() // For TEXT, TINYTEXT, MEDIUMTEXT, LONGTEXT
data class SQLEnumType(val allowedValues: List<String>) : SQLDataType() // For ENUM
data class SQLSetType(val allowedValues: Set<String>) : SQLDataType() // For SET

override fun serialize(graphState: IGraphState, schemaEntities: SchemaEntities) {
    val schemaEntity = schemaEntities[entityName]
    val graphEntities = graphState[entityName] ?: return

    graphEntities.forEach { graphEntity ->
        val columnNames = ArrayList<String>(schemaEntity.size)
        val values = ArrayList<String>()

        for (schemaIdx in schemaEntity.indices) {
            val possibleColumns = schemaEntity[schemaIdx]
            var found = false

            for (column in possibleColumns) {
                if (graphEntity.containsKey(column.columnName)) {
                    var value = formatColumn(column.columnName, graphEntity[column.columnName] ?: "")
                    if (column.length != -1) {
                        value = value.substring(0, minOf(value.length, column.length))
                    }
                    columnNames.add(column.columnName)
                    values.add(value)

                    found = true
                    break
                }
            }

            if (!found) {
                // TODO: Handle the case where the column is not found in the graph entity
            }
        }

        val columnsString = columnNames.joinToString(",")
        val valuesString = values.joinToString(",")

        val sqlStatement = "INSERT INTO $entityName ($columnsString) VALUES ($valuesString);"
        this.writer.write(sqlStatement)
    }

    this.writer.flush()
}


fun serialize(graphState: IGraphState) {
    schemaEntities.getEntities().forEach { (entityName, schemaEntity) ->
        val graphEntities = graphState[entityName] ?: return@forEach

        graphEntities.forEach { graphEntity ->
            val columnNames = ArrayList<String>()
            val values = ArrayList<String>()

            schemaEntity.getColumns().forEach { (columnName, column) ->
                if (graphEntity.containsKey(columnName)) {
                    var value = formatColumn(columnName, graphEntity[columnName] ?: "")
                    if (column.length != -1) {
                        value = value.substring(0, minOf(value.length, column.length))
                    }
                    columnNames.add(columnName)
                    values.add(value)
                }
            }

            val columnsString = columnNames.joinToString(",")
            val valuesString = values.joinToString(",")

            val sqlStatement = "INSERT INTO $entityName ($columnsString) VALUES ($valuesString);"
            writer.append(sqlStatement)
        }
    }
    writer.flush()
}

class UnsupportedTypeException(
        value: Any,
        expectedType: String
) : IllegalArgumentException("Unsupported type: ${value::class.simpleName}. Expected: $expectedType.")

// Ensure total number of digits does not exceed precision
val stringValue = bigDecimal.toPlainString()
val decimalIndex = stringValue.indexOf('.')
val endIndex = if (decimalIndex != -1) {
    minOf(decimalIndex + precision + 1, stringValue.length)
} else {
    minOf(precision, stringValue.length)
}

return stringValue.substring(0, endIndex)