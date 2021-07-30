package com.example.organizer.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_charts", indices = [Index(unique = true, value = ["chart_name"], name = "transaction_charts01")])
data class TransactionChart (
    @PrimaryKey val id:String,
    @ColumnInfo(name = "chart_name", typeAffinity = ColumnInfo.TEXT) var chartName: String,
    @ColumnInfo(name = "chart_type", typeAffinity = ColumnInfo.INTEGER) var chartType: Int,
    @ColumnInfo(name = "chart_order", typeAffinity = ColumnInfo.INTEGER) var chartOrder: Int,
    @ColumnInfo(name = "chart_entity", typeAffinity = ColumnInfo.INTEGER) var chartEntity: Int,
    @ColumnInfo(name = "start_after_transaction_id", typeAffinity = ColumnInfo.INTEGER) var startAfterTransactionId: Long,
    @ColumnInfo(name = "x_type", typeAffinity = ColumnInfo.INTEGER) var xType: Int,
    @ColumnInfo(name = "show_extra_one_point", typeAffinity = ColumnInfo.INTEGER) var showExtraOnePoint: Int,
    @ColumnInfo(name = "extra_point_label", typeAffinity = ColumnInfo.TEXT) var extraPointLabel: String?,
    @ColumnInfo(name = "filter_categories", typeAffinity = ColumnInfo.INTEGER) var filterCategories: Int,
    @ColumnInfo(name = "group_categories", typeAffinity = ColumnInfo.INTEGER) var groupCategories: Int,
    @ColumnInfo(name = "group_transaction_type", typeAffinity = ColumnInfo.INTEGER) var groupTransactionType: Int,
    @ColumnInfo(name = "schedule_interval_type", typeAffinity = ColumnInfo.INTEGER) var scheduleIntervalType: Int,
    @ColumnInfo(name = "generate_at", typeAffinity = ColumnInfo.INTEGER) var generateAt: Long?,
    @ColumnInfo(name = "last_generated_at", typeAffinity = ColumnInfo.INTEGER) var lastGeneratedAt: Long?,
    @ColumnInfo(name = "save_point", typeAffinity = ColumnInfo.INTEGER) var savePoint: Int
)