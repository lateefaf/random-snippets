val(root, entities) = ast({
    // User Entity
    column("User", "ID", 0, ColumnDataType.Integer, 4)
    column("User", "Email", 1, ColumnDataType.String, 50)
    column("User", "Name", 2, ColumnDataType.String, 100)
    column("User", "EligibleForCredit", 3, ColumnDataType.Boolean, 1)
    column("User", "CreditCardNumber", 4, ColumnDataType.String, 19)

    // Account Entity
    column("Account", "ID", 0, ColumnDataType.Integer, 4)
    column("Account", "UserID", 1, ColumnDataType.Integer, 4)
    column("Account", "Balance", 2, ColumnDataType.Double, 16)
    column("Account", "CreatedDate", 3, ColumnDataType.LocalDateTime, 32)

    // Transaction Entity
    column("Transaction", "ID", 0, ColumnDataType.Integer, 4)
    column("Transaction", "AccountID", 1, ColumnDataType.Integer, 4)
    column("Transaction", "Amount", 2, ColumnDataType.Double, 16)
    column("Transaction", "Timestamp", 3, ColumnDataType.LocalDateTime, 32)
    column("Transaction", "IsDeposit", 4, ColumnDataType.Boolean, 1)

    // CreditCard Entity
    column("CreditCard", "Number", 0, ColumnDataType.String, 19)
    column("CreditCard", "UserID", 1, ColumnDataType.Integer, 4)
    column("CreditCard", "ExpiryDate", 2, ColumnDataType.LocalDateTime, 32)
    column("CreditCard", "CVV", 3, ColumnDataType.Integer, 3)
}, {
    strategy = FixedRelationStrategy(100) // For example, generate data for 100 users

    assign1("User.ID"){
        strategy = CountingIntegerStrategy(1) // Starts ID at 1 and increments
    }
    assign1("User.Email"){
        strategy = EmailStrategy() // Uses faker to generate email
    }
    assign1("User.Name"){
        strategy = FullNameStrategy() // Uses faker to generate full name
    }
    assign1("User.EligibleForCredit"){
        strategy = RandomBooleanStrategy() // 50% chance to be eligible for credit
    }

    condition({ state -> (state["User", "EligibleForCredit"] as Boolean) }, {
        assign1("User.CreditCardNumber"){
            strategy = Big4CreditCardStrategy() // Generates credit card number if eligible
        }
    })

    assign1("Account.ID"){
        strategy = CountingIntegerStrategy(1000) // Unique Account IDs
    }
    assign1("Account.UserID"){
        strategy = SimpleReferenceStrategy("User.ID") // Links account to user
    }
    assign1("Account.Balance"){
        strategy = LinearRandomDoubleStrategy({min = -1000.0, max = 10000.0}) // Random balance
    }
    assign1("Account.CreatedDate"){
        strategy = LinearRandomLocalDateTimeStrategy() // Random account creation date
    }

    loop {
        strategy = FixedRelationStrategy(5) // Each user has 5 transactions

        assign1("Transaction.ID"){
            strategy = CountingIntegerStrategy(5000) // Unique Transaction IDs
        }
        assign1("Transaction.AccountID"){
            strategy = SimpleReferenceStrategy("Account.ID") // Links transaction to account
        }
        assign1("Transaction.Amount"){
            strategy = LinearRandomDoubleStrategy({min = -500.0, max = 500.0}) // Transaction amount
        }
        assign1("Transaction.Timestamp"){
            strategy = LinearRandomLocalDateTimeStrategy() // Timestamp of transaction
        }
        assign1("Transaction.IsDeposit"){
            strategy = RandomBooleanStrategy() // Randomly determines if transaction is a deposit
        }

        // Based on the IsDeposit value, the Amount can be positive or negative
        condition({ state -> (state["Transaction", "IsDeposit"] as Boolean) }, {
            assign1("Transaction.Amount"){
                strategy = LinearRandomDoubleStrategy({min = 0.01, max = 500.0}) // Deposit amount
            }
        }, {
            assign1("Transaction.Amount"){
                strategy = LinearRandomDoubleStrategy({min = -500.0, max = -0.01}) // Withdrawal amount
            }
        })
    }

    loop {
        strategy = FixedRelationStrategy(1) // Assumes 1 credit card per eligible user

        assign1("CreditCard.Number"){
            strategy = SimpleReferenceStrategy("User.CreditCardNumber") // Same as user's credit card number
        }
        assign1("CreditCard.UserID"){
            strategy = SimpleReferenceStrategy("User.ID") // Link to user
        }
        assign1("CreditCard.ExpiryDate"){
            strategy = LinearRandomLocalDateTimeStrategy({min = "2023-01-01T00:00:00", max = "2030-12-31T23:59:59"}) // Card expiry date
        }
        assign1("CreditCard.CVV"){
            strategy = LinearRandomIntegerStrategy({min = 100, max = 999}) // CVV for the card
        }
    }

    // ... Serializer configurations for writing the data to files ...
})
