package com.example.organizer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "template_transactions")
data class TemplateTransaction(
    @PrimaryKey val id:String,
    @ColumnInfo(name = "transaction_plan_id", typeAffinity = ColumnInfo.TEXT) var transactionPlanId: String,
    @ColumnInfo(name = "transaction_type", typeAffinity = ColumnInfo.INTEGER) var transactionType: Int,
    @ColumnInfo(name = "amount", typeAffinity = ColumnInfo.REAL) var amount: Double,
    @ColumnInfo(name = "from_account", typeAffinity = ColumnInfo.TEXT) var fromAccount: String?,
    @ColumnInfo(name = "to_account", typeAffinity = ColumnInfo.TEXT) var toAccount: String?,
    @ColumnInfo(name = "transaction_category_id", typeAffinity = ColumnInfo.TEXT) var transactionCategoryId: String?,
    @ColumnInfo(name = "details", typeAffinity = ColumnInfo.TEXT) var details: String?,
    @ColumnInfo(name = "order", typeAffinity = ColumnInfo.INTEGER) var order: Int
)