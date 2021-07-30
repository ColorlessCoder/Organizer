package com.example.organizer.database.dto

import androidx.room.ColumnInfo

data class CategoryWiseCount (
    @ColumnInfo(name = "sum", typeAffinity = ColumnInfo.REAL) val sum: Double,
    @ColumnInfo(name = "transaction_category_id", typeAffinity = ColumnInfo.TEXT) val transactionCategoryId: String?,
    @ColumnInfo(name = "transaction_type", typeAffinity = ColumnInfo.INTEGER) val transactionType: Int
)