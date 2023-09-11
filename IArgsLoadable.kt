interface IArgsLoadable<TSelf> where TSelf : IArgsLoadable<TSelf> {
    fun getArgs(): Map<String, ArgDefinition<TSelf, Any>>
}



