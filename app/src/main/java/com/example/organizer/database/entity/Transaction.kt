package com.example.organizer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey val id:String,
    @ColumnInfo(name = "transaction_type", typeAffinity = ColumnInfo.INTEGER) var transactionType: Int,
    @ColumnInfo(name = "amount", typeAffinity = ColumnInfo.REAL) var amount: Double,
    @ColumnInfo(name = "from_account", typeAffinity = ColumnInfo.TEXT) var fromAccount: String?,
    @ColumnInfo(name = "to_account", typeAffinity = ColumnInfo.TEXT) var toAccount: String?,
    @ColumnInfo(name = "scheduled_transaction_id", typeAffinity = ColumnInfo.TEXT) var scheduledTransactionId: String?,
    @ColumnInfo(name = "transaction_category_id", typeAffinity = ColumnInfo.TEXT) var transactionCategoryId: String?,
    @ColumnInfo(name = "details", typeAffinity = ColumnInfo.TEXT) var details: String?,
    @ColumnInfo(name = "transacted_at", typeAffinity = ColumnInfo.INTEGER) var transactedAt: Int
)