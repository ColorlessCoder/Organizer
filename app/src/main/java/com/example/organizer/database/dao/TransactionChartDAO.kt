package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Transaction
import com.example.organizer.database.dto.CategoryWiseCount
import com.example.organizer.database.entity.*
import com.example.organizer.database.enums.NoCategoryId
import com.example.organizer.database.enums.TransactionType
import java.util.*


@Dao
interface TransactionChartDAO : BaseDAO {
    @Query("Select * From transaction_charts ORDER BY chart_order ASC")
    fun getAllDebts(): LiveData<List<TransactionChart>>

    @Query("Select * From transaction_charts WHERE id=:id")
    fun getChartById(id: String): LiveData<TransactionChart>

    @Query("Select * From transaction_charts WHERE id=:id")
    suspend fun getChartByIdSuspend(id: String): TransactionChart

    @Update
    suspend fun update(chart: TransactionChart)

    @Insert
    suspend fun insert(chart: TransactionChart)

    @Insert
    suspend fun insert(value: TransactionChartValue)

    @Insert
    suspend fun insert(point: TransactionChartPoint): Long

    @Query("Select SUM(amount) as sum, transaction_category_id, transaction_type from transactions where id >= :fromTransactionId AND id <= :toTransactionId AND transaction_type != 2 Group BY transaction_category_id, transaction_type")
    suspend fun getCategoryCount(
        fromTransactionId: Long,
        toTransactionId: Long
    ): List<CategoryWiseCount>

    @Query("Select SUM(amount) as sum, transaction_category_id, transaction_type from transactions where id >= :fromTransactionId AND transaction_type != 2 Group BY transaction_category_id, transaction_type")
    suspend fun getCategoryCountUptoLatest(
        fromTransactionId: Long
    ): List<CategoryWiseCount>

    @Transaction
    suspend fun addChartPoint(point: TransactionChartPoint) {
        val pointId = insert(point)
        val chart = getChartByIdSuspend(point.chartId)
        val categoryWiseCount = getCategoryCount(point.fromTransactionId, point.toTransactionId)
        categoryWiseCount.forEach {
            insert(
                TransactionChartValue(
                    UUID.randomUUID().toString(),
                    pointId,
                    it.sum,
                    it.transactionCategoryId?:NoCategoryId.from(it.transactionType).label
                )
            )
        }
        chart.startAfterTransactionId = point.toTransactionId
        update(chart)
    }

    @Query("Select * from categories")
    suspend fun getAllCategories(): List<Category>

    @Query("Select * from transaction_charts order by chart_order ASC")
    fun getAllCharts(): LiveData<List<TransactionChart>>

    @Query("Select * from transaction_chart_points where chart_id = :chartId Order by id DESC LIMIT :limit")
    suspend fun getPointsForChart(chartId: String, limit: Long): MutableList<TransactionChartPoint>

    @Query("Select * from transaction_chart_values where point_id = :pointId")
    suspend fun getValuesForPointInChart(pointId: Long): List<TransactionChartValue>

    suspend fun getValuesForExtraPointInChart(mockPointId: Long, fromTransactionId: Long): List<TransactionChartValue> {
        val response = mutableListOf<TransactionChartValue>()
        val categoryWiseCount = getCategoryCountUptoLatest(fromTransactionId)
        categoryWiseCount.forEach {
            response.add(
                TransactionChartValue(
                    "UUID.randomUUID().toString()",
                    mockPointId,
                    it.sum,
                    it.transactionCategoryId?:NoCategoryId.from(it.transactionType).label
                )
            )
        }
        return response
    }
}