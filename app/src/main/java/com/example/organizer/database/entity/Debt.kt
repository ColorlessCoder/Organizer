package com.example.organizer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "debts")
data class Debt(
    @PrimaryKey val id:String,
    @ColumnInfo(name = "debt_type", typeAffinity = ColumnInfo.INTEGER) var debtType: Int,
    @ColumnInfo(name = "amount", typeAffinity = ColumnInfo.REAL) var amount: Double,
    @ColumnInfo(name = "paid_so_far", typeAffinity = ColumnInfo.REAL) var paidSoFar: Double,
    @ColumnInfo(name = "from_account", typeAffinity = ColumnInfo.TEXT) var fromAccount: String?,
    @ColumnInfo(name = "to_account", typeAffinity = ColumnInfo.TEXT) var toAccount: String?,
    @ColumnInfo(name = "details", typeAffinity = ColumnInfo.TEXT) var details: String,
    @ColumnInfo(name = "created_at", typeAffinity = ColumnInfo.INTEGER) var createdAt: Long,
    @ColumnInfo(name = "completed_at", typeAffinity = ColumnInfo.INTEGER) var completedAt: Long?,
    @ColumnInfo(name = "scheduled_at", typeAffinity = ColumnInfo.INTEGER) var scheduledAt: Long?
)