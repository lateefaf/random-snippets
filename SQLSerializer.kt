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
}