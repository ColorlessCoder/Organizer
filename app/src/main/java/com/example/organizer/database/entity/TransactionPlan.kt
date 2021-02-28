package com.example.organizer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_plans")
data class TransactionPlan(
    @PrimaryKey val id:String,
    @ColumnInfo(name = "name", typeAffinity = ColumnInfo.TEXT) var name: String,
    @ColumnInfo(name = "delete_after_execute", typeAffinity = ColumnInfo.INTEGER) var deleteAfterExecute: Int,
    @ColumnInfo(name = "color", typeAffinity = ColumnInfo.INTEGER) var color: Int
)