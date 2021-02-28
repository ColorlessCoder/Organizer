package com.example.organizer.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.organizer.database.entity.Account
import com.example.organizer.database.entity.Category
import com.example.organizer.database.entity.TemplateTransaction
import com.example.organizer.database.entity.Transaction

data class TemplateTransactionDetails (
    @Embedded val transaction: TemplateTransaction,
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
    val toAccountName: String?,
    @Relation(
        parentColumn = "transaction_category_id",
        entity = Category::class,
        entityColumn = "id",
        projection = ["category_name"]
    )
    val categoryName: String?
)