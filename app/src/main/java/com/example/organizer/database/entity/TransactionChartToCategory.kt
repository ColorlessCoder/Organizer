package com.example.organizer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_chart_to_category", indices = [Index(unique = true, value = ["chart_id", "category_id"], name = "transaction_chart_to_category_1")])
data class TransactionChartToCategory (
    @PrimaryKey val id:String,
    @ColumnInfo(name = "chart_id", typeAffinity = ColumnInfo.TEXT) var chart_id: String,
    @ColumnInfo(name = "category_id", typeAffinity = ColumnInfo.TEXT) var category_id: String
)