```lua
                +------------------+
                |     Customer     |
                +------------------+
                | CustomerID (PK)  |
                | Name             |
                | Address          |
                +--------+---------+
                         |
                         |
         +---------------+----------------+
         |                                |
+--------v---------+            +---------v--------+
|      Account     |            |    Transaction   |
+------------------+            +------------------+
| AccountNumber    |            | TransactionID    |
| CustomerID (FK)  +------------> AccountNumber    |
| Balance          |            | Type             |
| BranchID (FK)    |            | Amount           |
+------------------+            | Date             |
                                | LoanID (FK)      |
                                +---------+--------+
                                          |
                                          |
                                +---------v--------+
                                |       Loan       |
                                +------------------+
                                | LoanID (PK)      |
                                | CustomerID (FK)  |
                                | AccountNumber    |
                                | Amount           |
                                | InterestRate     |
                                +---------+--------+


```

In this diagram, the arrows represent the relationships between entities. Here's a breakdown of the relationships:

    One Customer can have multiple Accounts (one-to-many relationship, CustomerID in Account table references CustomerID in Customer table).
    One Account can have multiple Transactions (one-to-many relationship, AccountNumber in Transaction table references AccountNumber in Account table).
    One Transaction can be associated with one Loan (one-to-one relationship, LoanID in Transaction table references LoanID in Loan table).
    One Loan can be associated with one Customer (one-to-one relationship, CustomerID in Loan table references CustomerID in Customer table).
    One Loan can be associated with one Account (one-to-one relationship, AccountNumber in Loan table references AccountNumber in Account table).