package com.example.organizer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings", indices = [Index(unique = true, value = ["settings_name"])])
data class UserSettings(
    @PrimaryKey val id:String,
    @ColumnInfo(name = "settings_name", typeAffinity = ColumnInfo.TEXT) var settingsName: String,
    @ColumnInfo(name = "country", typeAffinity = ColumnInfo.TEXT) var country: String,
    @ColumnInfo(name = "city", typeAffinity = ColumnInfo.TEXT) var city: String,
    @ColumnInfo(name = "active", typeAffinity = ColumnInfo.INTEGER) var active: Int
)