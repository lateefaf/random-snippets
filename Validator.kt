import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// Define a callback interface for validation
interface ValidationCallback<T> {
    fun onValid(value: T)
    fun onInvalid(errorMessage: String)
}

// Validator class
class Validator {
    private val executor = Executors.newSingleThreadExecutor()

    fun validateValue(value: Any, callback: ValidationCallback<Any>) {
        executor.submit {
            try {
                when (value) {
                    is Int -> {
                        callback.onValid(value)
                    }
                    is Double -> {
                        callback.onValid(value)
                    }
                    is String -> {
                        // Check if the string can be converted to a double
                        try {
                            val doubleValue = value.toDouble()
                            callback.onValid(doubleValue)
                        } catch (e: NumberFormatException) {
                            callback.onInvalid("Invalid double format")
                        }
                    }
                    else -> {
                        callback.onInvalid("Invalid value type")
                    }
                }
            } catch (e: Exception) {
                callback.onInvalid(e.message ?: "An error occurred during validation.")
            }
        }
    }
}

fun main() {
    val validator = Validator()

    val valueToValidate = "42.5"

    val validationCallback = object : ValidationCallback<Any> {
        override fun onValid(value: Any) {
            println("Validation passed. Valid value: $value")
        }

        override fun onInvalid(errorMessage: String) {
            println("Validation failed. Error: $errorMessage")
        }
    }

    // Start the validation
    validator.validateValue(valueToValidate, validationCallback)

    // Continue with other tasks while validation is in progress
}
