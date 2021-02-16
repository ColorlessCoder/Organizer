package com.example.organizer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "accounts", indices = [Index(unique = true, value = ["account_name"])])
data class Account(
    @PrimaryKey val id:String,
    @ColumnInfo(name = "account_name", typeAffinity = ColumnInfo.TEXT) var accountName: String,
    @ColumnInfo(name = "balance", typeAffinity = ColumnInfo.REAL) var balance: Double,
    @ColumnInfo(name = "background_color", typeAffinity = ColumnInfo.INTEGER) var backgroundColor: Int,
    @ColumnInfo(name = "font_color", typeAffinity = ColumnInfo.INTEGER) var fontColor: Int,
    @ColumnInfo(name = "unit", typeAffinity = ColumnInfo.TEXT) var unit: String
)