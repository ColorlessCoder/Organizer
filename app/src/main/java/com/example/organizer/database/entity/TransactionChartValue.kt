package com.example.organizer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_chart_values")
data class TransactionChartValue (
    @PrimaryKey val id:String,
    @ColumnInfo(name = "point_id", typeAffinity = ColumnInfo.INTEGER) var pointId: Long,
    @ColumnInfo(name = "value", typeAffinity = ColumnInfo.REAL) var value: Double,
    @ColumnInfo(name = "entity_id", typeAffinity = ColumnInfo.TEXT) var entityId: String?
)