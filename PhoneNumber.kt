class PhoneNumber {
    fun generatePhoneNumber(areaCode: String? = null): String {
        val faker = Faker()
        val generatedPhoneNumber: String

        if (areaCode != null) {
            // Use the specified area code with the last 7 digits from Faker
            val lastSevenDigits = faker.phoneNumber.cellPhone().substring(3)
            generatedPhoneNumber = "$areaCode$lastSevenDigits"
        } else {
            // Generate a complete 10-digit phone number using Faker
            generatedPhoneNumber = faker.phoneNumber.cellPhone().replace("-", "")
        }

        return generatedPhoneNumber
    }
}