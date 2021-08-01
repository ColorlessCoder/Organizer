package com.example.organizer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "salat_times", indices = [Index(unique = true, value = ["address", "date"])])
data class SalatTime(
    @PrimaryKey var id:String,
    @ColumnInfo(name = "address", typeAffinity = ColumnInfo.TEXT) var address: String,
    @ColumnInfo(name = "date", typeAffinity = ColumnInfo.TEXT) var date: String,
    @ColumnInfo(name = "fajr_start", typeAffinity = ColumnInfo.TEXT) var fajrStart: String,
    @ColumnInfo(name = "sunrise", typeAffinity = ColumnInfo.TEXT) var sunrise: String,
    @ColumnInfo(name = "dhuhr_start", typeAffinity = ColumnInfo.TEXT) var dhuhrStart: String,
    @ColumnInfo(name = "asr_start", typeAffinity = ColumnInfo.TEXT) var asrStart: String,
    @ColumnInfo(name = "sunset", typeAffinity = ColumnInfo.TEXT) var sunset: String,
    @ColumnInfo(name = "maghrib_start", typeAffinity = ColumnInfo.TEXT) var maghribStart: String,
    @ColumnInfo(name = "isha_start", typeAffinity = ColumnInfo.TEXT) var ishaStart: String,
    @ColumnInfo(name = "imsak", typeAffinity = ColumnInfo.TEXT) var imsak: String,
    @ColumnInfo(name = "midnight", typeAffinity = ColumnInfo.TEXT) var midnight: String
)