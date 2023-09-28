Initialization Steps

    An empty graph object is created.
    A GraphState object is initialized to keep track of the synthetic data that will be generated.

Graph Vertices Creation

    User Vertex
        GraphVertex object is created with entityName set to "User" and columns containing metadata for User.Id and User.Name.

    Transaction Vertex
        Another GraphVertex object is created with entityName set to "Transaction" and columns containing metadata for Transaction.Id, Transaction.UserId, and Transaction.Value.

Establishing Relationships

    In the Transaction vertex, the column Transaction.UserId will have a reference column pointing to User.Id. This establishes the many-to-one relationship between Transactions and Users.

Graph Edges Creation

    An edge is created from the Transaction vertex to the User vertex, indicating that the Transaction entity is dependent on the User entity. This is because Transaction.UserId is a foreign key referencing User.Id.

Node Operations

For each vertex, the following nodes are constructed:

    AssignmentOperation Nodes: For each column in the entity, an AssignmentOperation node is created, responsible for generating synthetic data for that column.

    LoopOperation Nodes: For each entity, a LoopOperation node is created, which will dictate how many rows of synthetic data will be created for that entity.

    WriteOperation Nodes: For each entity, a WriteOperation node is created, which will write the synthetic data to the database or a file.

    PushState Nodes: These nodes push the current graph state to the stack, allowing us to keep track of the synthetic data generated for the parent entities when generating child entities.

Node Ordering

    User's AssignmentOperation nodes for User.Id and User.Name are placed first, followed by the User's WriteOperation node.
    A PushState node is added before moving to the Transaction entity.
    Transaction's AssignmentOperation nodes are added for Transaction.Id, Transaction.UserId, and Transaction.Value, followed by the Transaction's WriteOperation node.

Finalization

    The nodes are organized into an array, respecting the order of dependencies, and the array is assigned to the nodes property of the graph object.

Graph Evaluation

    The GraphEvaluator takes the graph object and begins evaluation.
    It traverses each node in the nodes array, performing the operation defined by each node.
    The GraphState object is used to keep track of the synthetic data generated so far.
    For each Transaction.UserId, the evaluator picks a User.Id from the GraphState, maintaining the many-to-one relationship.