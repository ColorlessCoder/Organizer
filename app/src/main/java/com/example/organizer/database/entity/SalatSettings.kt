package com.example.organizer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "salat_settings", indices = [Index(unique = true, value = ["settings_name"])])
data class SalatSettings(
    @PrimaryKey val id:String,
    @ColumnInfo(name = "settings_name", typeAffinity = ColumnInfo.TEXT) var settingsName: String,
    @ColumnInfo(name = "address", typeAffinity = ColumnInfo.TEXT) var address: String,
    @ColumnInfo(name = "active", typeAffinity = ColumnInfo.INTEGER) var active: Int,
    @ColumnInfo(name = "salat_alert", typeAffinity = ColumnInfo.INTEGER) var salatAlert: Int,
    @ColumnInfo(name = "fajr_alert", typeAffinity = ColumnInfo.INTEGER) var fajrAlert: Int,
    @ColumnInfo(name = "dhuhr_alert", typeAffinity = ColumnInfo.INTEGER) var dhuhrAlert: Int,
    @ColumnInfo(name = "asr_alert", typeAffinity = ColumnInfo.INTEGER) var asrAlert: Int,
    @ColumnInfo(name = "maghrib_alert", typeAffinity = ColumnInfo.INTEGER) var maghribAlert: Int,
    @ColumnInfo(name = "isha_alert", typeAffinity = ColumnInfo.INTEGER) var ishaAlert: Int,
    @ColumnInfo(name = "fajr_safety", typeAffinity = ColumnInfo.INTEGER) var fajrSafety: Int,
    @ColumnInfo(name = "dhuhr_safety", typeAffinity = ColumnInfo.INTEGER) var dhuhrSafety: Int,
    @ColumnInfo(name = "asr_safety", typeAffinity = ColumnInfo.INTEGER) var asrSafety: Int,
    @ColumnInfo(name = "maghrib_safety", typeAffinity = ColumnInfo.INTEGER) var maghribSafety: Int,
    @ColumnInfo(name = "isha_safety", typeAffinity = ColumnInfo.INTEGER) var ishaSafety: Int,
    @ColumnInfo(name = "sunrise_redzone", typeAffinity = ColumnInfo.INTEGER) var sunriseRedzone: Int,
    @ColumnInfo(name = "midday_redzone", typeAffinity = ColumnInfo.INTEGER) var middayRedzone: Int,
    @ColumnInfo(name = "sunset_redzone", typeAffinity = ColumnInfo.INTEGER) var sunsetRedzone: Int
)