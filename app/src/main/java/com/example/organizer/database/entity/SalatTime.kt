package com.example.organizer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "salat_times")
data class SalatTime(
    @PrimaryKey var id:String,
    @ColumnInfo(name = "city", typeAffinity = ColumnInfo.TEXT) var city: String,
    @ColumnInfo(name = "country", typeAffinity = ColumnInfo.TEXT) var country: String,
    @ColumnInfo(name = "date", typeAffinity = ColumnInfo.TEXT) var date: String,
    @ColumnInfo(name = "tahajjud_start", typeAffinity = ColumnInfo.TEXT) var tahajjudStart: String,
    @ColumnInfo(name = "tahajjud_end", typeAffinity = ColumnInfo.TEXT) var tahajjudEnd: String,
    @ColumnInfo(name = "fajr_start", typeAffinity = ColumnInfo.TEXT) var fajrStart: String,
    @ColumnInfo(name = "fajr_end", typeAffinity = ColumnInfo.TEXT) var fajrEnd: String,
    @ColumnInfo(name = "sunrise", typeAffinity = ColumnInfo.TEXT) var sunrise: String,
    @ColumnInfo(name = "first_restriction_start", typeAffinity = ColumnInfo.TEXT) var firstRestrictionStart: String,
    @ColumnInfo(name = "first_restriction_end", typeAffinity = ColumnInfo.TEXT) var firstRestrictionEnd: String,
    @ColumnInfo(name = "ishraq_start", typeAffinity = ColumnInfo.TEXT) var ishraqStart: String,
    @ColumnInfo(name = "ishraq_end", typeAffinity = ColumnInfo.TEXT) var ishraqEnd: String,
    @ColumnInfo(name = "midday", typeAffinity = ColumnInfo.TEXT) var midday: String,
    @ColumnInfo(name = "second_restriction_start", typeAffinity = ColumnInfo.TEXT) var secondRestrictionStart: String,
    @ColumnInfo(name = "second_restriction_end", typeAffinity = ColumnInfo.TEXT) var secondRestrictionEnd: String,
    @ColumnInfo(name = "dhuhr_start", typeAffinity = ColumnInfo.TEXT) var dhuhrStart: String,
    @ColumnInfo(name = "dhuhr_end", typeAffinity = ColumnInfo.TEXT) var dhuhrEnd: String,
    @ColumnInfo(name = "asr_start", typeAffinity = ColumnInfo.TEXT) var asrStart: String,
    @ColumnInfo(name = "asr_end", typeAffinity = ColumnInfo.TEXT) var asrEnd: String,
    @ColumnInfo(name = "sunset", typeAffinity = ColumnInfo.TEXT) var sunset: String,
    @ColumnInfo(name = "third_restriction_start", typeAffinity = ColumnInfo.TEXT) var thirdRestrictionStart: String,
    @ColumnInfo(name = "third_restriction_end", typeAffinity = ColumnInfo.TEXT) var thirdRestrictionEnd: String,
    @ColumnInfo(name = "maghrib_start", typeAffinity = ColumnInfo.TEXT) var maghribStart: String,
    @ColumnInfo(name = "maghrib_end", typeAffinity = ColumnInfo.TEXT) var maghribEnd: String,
    @ColumnInfo(name = "isha_start", typeAffinity = ColumnInfo.TEXT) var ishaStart: String,
    @ColumnInfo(name = "isha_end", typeAffinity = ColumnInfo.TEXT) var ishaEnd: String,
    @ColumnInfo(name = "imsak", typeAffinity = ColumnInfo.TEXT) var imsak: String
)