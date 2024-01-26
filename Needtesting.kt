class LinearRandomLocalDateTimeStrategy(
    private var min: LocalDateTime = LocalDateTime.now().minusYears(1),
    private var max: LocalDateTime = LocalDateTime.now()
): AbstractSingleColumnStrategy<LocalDateTime>(), IArgsValidatable{
    private var nanoRange: Long = 0L

    override fun validateArguments(schema: SchemaEntities){
        if(this.max < this.min){
            throw InvalidBoundsException("min", min.nano, "max", max.nano)
        }
        val period = Period.between(min.toLocalDate(), max.toLocalDate())

        if (period.years > 291){
            throw RangeTooLargeException()
        }
    }

    override fun onConstructOrLoad(applicationContext: ApplicationContext){
        super.onConstructOrLoad(applicationContext)

        this.nanoRange = this.min.until(this.max, ChronoUnit.NANOS)
    }

    override fun produce(graphState: IGraphState): LocalDateTime{
        val nanoseconds = this.faker.random().nextLong(nanoRange)
        return this.min.plusNanos(nanoseconds)
    }
}

class FullNameFourColumn : AbstractMultiColumnStrategy(){
    companion object{
        const val ARG_NAME_FIRST_NAME_COLUMN = "first_name"
        const val ARG_NAME_MIDDLE_NAME_COLUMN = "middle_name"
        const val ARG_NAME_LAST_NAME_COLUMN = "last_name"
        const val ARG_NAME_FULL_NAME_COLUMN = "full_name"
    }

    override fun produce(graphState: IGraphState): Map<String>, Any? {
        val first = faker.name().firstName()
        val middle = faker.name().middleName()
        val last = faker.name().lastName()
        val full = "$first $middle $last"

        return mapOf(
            ARG_NAME_FIRST_NAME_COLUMN to first,
            ARG_NAME_MIDDLE_NAME_COLUMN to middle,
            ARG_NAME_LAST_NAME_COLUMN to last,
            ARG_NAME_FULL_NAME_COLUMN to full
        )
    }

    override fun getVirtualColumns(): Set<String>{
        return setOf(
            ARG_NAME_FIRST_NAME_COLUMN, ARG_NAME_MIDDLE_NAME_COLUMN, ARG_NAME_LAST_NAME_COLUMN, ARG_NAME_FULL_NAME_COLUMN
        )
    }
}