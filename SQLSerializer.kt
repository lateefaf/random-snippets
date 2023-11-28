class SQLSerializer(
        private val tableName: String,
        private val appendMode: Boolean = false,
        private val extension: String = ".sql"
) {
    // Assumed Writer Initialization (Could be a File writer or similar)
    private val writer = StringBuilder()

    fun serialize(graphState: IGraphState, schemaEntities: SchemaEntities) {
        schemaEntities.forEach { (entityName, schemaEntity) ->
            val graphEntity = graphState[entityName] ?: return

            val columnNames = ArrayList<String>()
            val values = ArrayList<String>()

            for (schemaColumn in schemaEntity) {
                graphEntity[schemaColumn.columnName]?.let { value ->
                    columnNames.add(schemaColumn.columnName)
                    values.add(formatColumn(schemaColumn, value))
                }
            }

            val columnsString = columnNames.joinToString(", ")
            val valuesString = values.joinToString(", ")

            val sqlStatement = "INSERT INTO $tableName ($columnsString) VALUES ($valuesString);"
            write(sqlStatement)
        }
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

class SQLSerializer(
        private val tableName: String,
        private val columnDataTypes: Map<String, String>, // Add this parameter
        private val appendMode: Boolean = false,
        private val extension: String = ".sql"
) {
    private val writer = StringBuilder()

    fun serialize(graphState: IGraphState, schemaEntities: SchemaEntities, entityName: String) {
        // ... [Serialization logic]
    }

    private fun formatColumn(columnName: String, value: Any): String {
        val dataType = columnDataTypes[columnName] ?: return value.toString()

        return when (dataType) {
            "CHAR", "VARCHAR", "TEXT", "TINYTEXT", "MEDIUMTEXT" -> formatString(value.toString(), /* Assume length from somewhere */)
            "VARBINARY" -> formatVarBinary(value as ByteArray, /* Assume length from somewhere */)
            // ... other data types
            "INT", "TINYINT", "SMALLINT", "MEDIUMINT", "BIGINT" -> castToInt(value, dataType)
            // ... add other data types as needed
            else -> value.toString()
        }
    }

    private fun castToInt(value: Any, dataType: String): String {
        // ... [castToInt implementation]
    }

    // ... [Other formatting methods]
}

// Usage Example
val columnTypes = mapOf("columnName1" to "VARCHAR", "columnName2" to "INT")
val serializer = SQLSerializer("tableName", columnTypes)
