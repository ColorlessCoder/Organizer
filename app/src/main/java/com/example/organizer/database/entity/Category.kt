package com.example.organizer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val id:String,
    @ColumnInfo(name = "category_name", typeAffinity = ColumnInfo.TEXT) var categoryName: String,
    @ColumnInfo(name = "transaction_type", typeAffinity = ColumnInfo.INTEGER) var transactionType: Int,
    @ColumnInfo(name = "background_color", typeAffinity = ColumnInfo.INTEGER) var backgroundColor: Int,
    @ColumnInfo(name = "font_color", typeAffinity = ColumnInfo.INTEGER) var fontColor: Int
)