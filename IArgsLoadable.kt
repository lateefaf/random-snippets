interface IArgsLoadable<TSelf> where TSelf : IArgsLoadable<TSelf> {
    fun getArgs(): Map<String, ArgDefinition<TSelf, Any>>
}



else{
    //here we are not IArgsLoadable - we don't want the user to expect changing behavior with arguments that do nothing, so if they provide args we throw
    if(specification.args.isNotBlank()){
        // Throw custom exception
    }
}