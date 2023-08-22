# Project Structure
The project is divided into the following packages:
1. **Graph Package** - Deals with graph-related functionalities and nodes.
2. **Lifecycle Package** - Manages the lifecycle of the components.
3. **Relationship Package** - Handles the relationships and strategy for relationships.

## Graph Package

### Classes & Interfaces
1. `INode` (Interface): The generic node interface that represents a node in the graph.
2. `AbstractGraphNode`: An abstract class representing a generic graph node.
3. `AggregateNode`: A specialized node for aggregating data.
4. `ExecutingNode`: A specialized node for executing specific operations.
5. `SerializingNode`: A specialized node for serializing data.
6. `GraphState`: Manages the state of the graph.
7. `IVisit` (Interface): Represents a visitor pattern, allowing operations to be performed on nodes.

### Methods & Relationships
- `INode`:
    - `val children: MutableSet<INode>`
    - `fun isInternal()`
    - `fun isExternal()`
    - `fun requires(other: INode)`
    - `fun requireBy(other: INode)`
    - `fun execute(graphState: GraphState)`
    - `fun accept(visitor: IVisitor)`

- `AbstractGraphNode`: Common attributes and methods shared among nodes.
- `AggregateNode`, `ExecutingNode`, `SerializingNode`: Implementation-specific methods and attributes.

- `GraphState`: Maintains the state, managing nodes, and their connections.

- `IVisit`: Methods like `visit()`, allowing specialized behavior for different nodes.

## Lifecycle Package

### Classes & Interfaces
1. `IConstructCallback` (Interface): Callback interface called immediately after object construction.
2. `ILoadCallback` (Interface): Executed right after loading all objects from disk.
3. `IConstructOrLoadCallback` (Interface): Callback interface for events related to construction or loading.
4. `ISaveCallback` (Interface): Executed right after saving all objects to disk.
5. **Nested Package: Visitor**
    - `AfterLoadVisitor`: Class handling behavior after loading.
    - `BeforeSaveCallback`: Class handling behavior before saving.

### Methods & Relationships
- `IConstructCallback`, `ILoadCallback`, `IConstructOrLoadCallback`, `ISaveCallback`: Define appropriate callback methods.
- `AfterLoadVisitor`, `BeforeSaveCallback`: Define specific visitor logic.

## Relationship Package

### Classes & Interfaces
1. `FixedRelationStrategy`: Strategy for fixed relationships between entities.
2. `IRelationshipStrategy` (Interface): Interface for defining different relationship strategies.

### Methods & Relationships
- `IRelationshipStrategy`: `iterationCount(graphState: GraphState)`
- `FixedRelationStrategy`: Implements the methods defined in `IRelationshipStrategy`, specifically for fixed relationships.
