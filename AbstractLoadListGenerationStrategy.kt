abstract class AbstractLoadListGenerationStrategy<T>: AbstractSingleColumnStrategy<T>(), IConstructorOrLoadCallback {
    lateinit var options: Array<T>
        private set

    // Overridable property for subclasses to specify their resource path
    open val resourcePath: String
        get() = "/data/list/default.txt" // Default path, can be overridden

    override fun onConstructOrLoad(applicationContext: ApplicationContext) {
        super.onConstructOrLoad(applicationContext)
        this.faker = applicationContext.getBean(Faker::class.java)
        this.random = applicationContext.getBean(Random::class.java)
        this.options = convert(loadResourceFile())
    }

    override fun produce(): T {
        return this.faker.options().nextElement(this.options)
    }

    private fun loadResourceFile(): List<String> {
        val resourceStream = javaClass.getResourceAsStream(this.resourcePath) ?: TODO()
        val streamReader = InputStreamReader(resourceStream)
        val lines = streamReader.readLines() as MutableList<String>
        if (lines[lines.lastIndex].isEmpty()) {
            lines.removeAt(lines.lastIndex)
        }
        return lines
    }

    protected abstract fun convert(values: List<String>): Array<T>
}
