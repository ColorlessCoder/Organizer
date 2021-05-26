package com.example.organizer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_chart_points")
data class TransactionChartPoint (
    @PrimaryKey(autoGenerate = true) val id:Long,
    @ColumnInfo(name = "chart_id", typeAffinity = ColumnInfo.TEXT) var chartId: String,
    @ColumnInfo(name = "label", typeAffinity = ColumnInfo.TEXT) var label: String,
    @ColumnInfo(name = "created_at", typeAffinity = ColumnInfo.INTEGER) var createdAt: Long,
    @ColumnInfo(name = "from_transaction_id", typeAffinity = ColumnInfo.INTEGER) var fromTransactionId: Long,
    @ColumnInfo(name = "to_transaction_id", typeAffinity = ColumnInfo.INTEGER) var toTransactionId: Long
)
