package com.example.organizer.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Transaction

data class TransactionDetails (
    @Embedded val transaction: Transaction,
    @Relation(
        parentColumn = "from_account",
        entity = Account::class,
        entityColumn = "id",
        projection = ["account_name"]
    )
    val fromAccountName: String?,
    @Relation(
        parentColumn = "to_account",
        entity = Account::class,
        entityColumn = "id",
        projection = ["account_name"]
    )
    val toAccountName: String?
)