package com.example.organizer.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Transaction
import com.example.organizer.database.dto.CategoryWiseCount
import com.example.organizer.database.entity.*
import com.example.organizer.database.enums.NoCategoryId
import com.example.organizer.database.enums.TransactionType
import com.example.organizer.ui.money.viewTransaction.ViewTransactionGraphViewModel.Companion.NO_CATEGORY
import java.util.*


@Dao
interface TransactionChartDAO : BaseDAO {
    companion object {
        const val selectForCategoryCount =
            "Select SUM(transactions.amount) as sum, transactions.transaction_category_id, transactions.transaction_type from transactions LEFT JOIN debts ON debts.id = transactions.debt_id"
        const val conditionForCategoryCount =
            "transaction_type != 2 AND (transactions.debt_id IS NULL OR debts.debt_type = 3)"
        const val groupForCategoryCount =
            "transactions.transaction_category_id, transactions.transaction_type"
    }

    @Query("Select * From transaction_charts ORDER BY chart_order ASC")
    fun getAllDebts(): LiveData<List<TransactionChart>>

    @Query("Select * From transaction_charts WHERE id=:id")
    fun getChartById(id: String): LiveData<TransactionChart>

    @Query("Select * From transaction_charts WHERE id=:id")
    suspend fun getChartByIdSuspend(id: String): TransactionChart

    @Query("Select * From transaction_chart_points WHERE id=:id")
    suspend fun getPointByIdSuspend(id: Long): TransactionChartPoint

    @Update
    suspend fun update(chart: TransactionChart)

    @Insert
    suspend fun insert(chart: TransactionChart)

    @Insert
    suspend fun insert(value: TransactionChartValue)

    @Insert
    suspend fun insert(point: TransactionChartPoint): Long

    @Query("$selectForCategoryCount where transactions.id >= :fromTransactionId AND transactions.id <= :toTransactionId AND $conditionForCategoryCount Group BY $groupForCategoryCount")
    suspend fun getCategoryCount(
        fromTransactionId: Long,
        toTransactionId: Long
    ): List<CategoryWiseCount>

    @Query("$selectForCategoryCount where transactions.id >= :fromTransactionId AND $conditionForCategoryCount Group BY $groupForCategoryCount")
    suspend fun getCategoryCountUptoLatest(
        fromTransactionId: Long
    ): List<CategoryWiseCount>

    @Transaction
    suspend fun addChartPoint(point: TransactionChartPoint, updateChart: Boolean) {
        val pointId = insert(point)
        val chart = getChartByIdSuspend(point.chartId)
        generateValuesForPoint(point, chart, pointId)
        if (updateChart) {
            chart.startAfterTransactionId = point.toTransactionId
            update(chart)
        }
    }

    @Transaction
    suspend fun generateValuesForPoint(
        point: TransactionChartPoint,
        chart: TransactionChart,
        pointId: Long
    ) {
        val categories = getCategoriesRelatedToChart(chart.id).map { it.category_id }.toSet()
        val categoryWiseCount = getCategoryCount(point.fromTransactionId, point.toTransactionId)
        categoryWiseCount.forEach {
            val value = TransactionChartValue(
                UUID.randomUUID().toString(),
                pointId,
                it.sum,
                it.transactionCategoryId ?: NoCategoryId.from(it.transactionType).label
            )
            if (chart.filterCategories != 1 || categories.contains(value.entityId)) {
                insert(value)
            }
        }
    }

    @Update
    suspend fun update(transactionChartPoint: TransactionChartPoint)

    @Query("Select * from categories")
    suspend fun getAllCategories(): List<Category>

    @Query("Select c.* from transaction_chart_to_category tcc LEFT JOIN categories c ON tcc.category_id = c.id WHERE tcc.chart_id = :chartId AND tcc.category_id NOT LIKE '%${NO_CATEGORY}%'")
    suspend fun getCategoriesRelatedToChartWithoutNC(chartId: String): List<Category>

    @Query("Select * from transaction_chart_to_category WHERE chart_id = :chartId AND category_id LIKE '%${NO_CATEGORY}%'")
    suspend fun getNoCategoriesRelatedToChart(chartId: String): List<TransactionChartToCategory>

    @Transaction
    suspend fun getCategoriesForChart(chartId: String): List<Category> {
        val chart = getChartByIdSuspend(chartId)
        if (chart.filterCategories == 1) {
            val categories = getCategoriesRelatedToChartWithoutNC(chartId).toMutableList()
            getNoCategoriesRelatedToChart(chartId)
                .forEach { categories.add(NoCategoryId.fromLabel(it.category_id).toCategory()) }
            return categories
        }
        val categories = getAllCategories().toMutableList()
        mutableListOf(-1, 0, 1)
            .forEach { categories.add(NoCategoryId.from(it).toCategory()) }
        return categories
    }

    @Query("Select * from transaction_charts order by chart_order ASC")
    fun getAllCharts(): LiveData<List<TransactionChart>>

    @Query("Select * from transaction_chart_points where chart_id = :chartId Order by id DESC LIMIT :limit")
    suspend fun getPointsForChart(chartId: String, limit: Long): MutableList<TransactionChartPoint>

    @Query("Select * from transaction_chart_points where chart_id = :chartId Order by id DESC")
    suspend fun getAllPointsForChart(chartId: String): MutableList<TransactionChartPoint>

    @Query("Select * from transaction_chart_values where point_id = :pointId")
    suspend fun getValuesForPointInChart(pointId: Long): List<TransactionChartValue>

    @Query("Select * from transaction_chart_to_category where chart_id = :chartId")
    suspend fun getCategoriesRelatedToChart(chartId: String): List<TransactionChartToCategory>

    suspend fun getValuesForExtraPointInChart(
        mockPointId: Long,
        fromTransactionId: Long,
        chartId: String
    ): List<TransactionChartValue> {
        val response = mutableListOf<TransactionChartValue>()
        val categoryWiseCount = getCategoryCountUptoLatest(fromTransactionId)
        val chart = getChartByIdSuspend(chartId)
        val categories = getCategoriesRelatedToChart(chartId).map { it.category_id }.toSet()
        categoryWiseCount.forEach {
            val value = TransactionChartValue(
                "",
                mockPointId,
                it.sum,
                it.transactionCategoryId ?: NoCategoryId.from(it.transactionType).label
            )
            if (chart.filterCategories != 1 || categories.contains(value.entityId)) {
                response.add(value)
            }
        }
        return response
    }

    @Query("Delete from transaction_chart_to_category where category_id = :categoryId and chart_id = :chartId")
    suspend fun deleteChartToCategory(chartId: String, categoryId: String)

    @Insert
    suspend fun insert(chartToCategory: TransactionChartToCategory)

    @Transaction
    suspend fun saveChartToCategory(chartId: String, categories: Set<String>) {
        val existingSet = getCategoriesRelatedToChart(chartId).map { it.category_id }.toSet()
        existingSet
            .filter { !categories.contains(it) }
            .forEach { deleteChartToCategory(chartId, it) }
        categories
            .filter { !existingSet.contains(it) }
            .forEach {
                insert(
                    TransactionChartToCategory(
                        UUID.randomUUID().toString(),
                        chartId,
                        it
                    )
                )
            }
    }

    @Query("Select max(chart_order) from transaction_charts")
    suspend fun getMaxChartOrder(): Int?

    @Transaction
    suspend fun saveTransactionChart(
        chart: TransactionChart,
        insert: Boolean,
        cloneChartId: String?,
        categories: Set<String>?
    ) {
        if (insert) {
            val order = getMaxChartOrder()
            chart.chartOrder = (order ?: 0) + 1
            insert(chart)
        } else {
            update(chart)
        }
        if (categories != null && chart.filterCategories == 1) {
            saveChartToCategory(chart.id, categories)
        }
        if (insert && !cloneChartId.isNullOrEmpty()) {
            val points = getAllPointsForChart(cloneChartId)
            points.sortBy { it.id }
            points
                .forEach {
                    val newPoint = it.copy()
                    newPoint.id = 0L
                    newPoint.chartId = chart.id
                    addChartPoint(newPoint, false)
                }
        }
    }

    @Query("Delete From transaction_charts where id=:chartId")
    suspend fun deleteOnlyChartById(chartId: String)

    @Query("Delete From transaction_chart_values where point_id in (select tcp.id from transaction_chart_points tcp where tcp.chart_id=:chartId)")
    suspend fun deleteValuesOfChart(chartId: String)

    @Query("Delete From transaction_chart_to_category where chart_id=:chartId")
    suspend fun deleteCategoriesOfChart(chartId: String)

    @Query("Delete From transaction_chart_points where chart_id=:chartId")
    suspend fun deletePointsOfChart(chartId: String)

    @Transaction
    suspend fun deleteChart(chartId: String) {
        val chart = getChartByIdSuspend(chartId)
        deleteValuesOfChart(chartId)
        deleteCategoriesOfChart(chartId)
        deletePointsOfChart(chartId)
        deleteOnlyChartById(chartId)
        decreaseChartOrders(chart.chartOrder)
    }

    @Query("Update transaction_charts SET chart_order = chart_order - 1 WHERE chart_order > :order")
    suspend fun decreaseChartOrders(order: Int)

    @Query("Delete from transaction_chart_values where point_id = :pointId")
    suspend fun deleteValuesForPoint(pointId: Long)

    @Query("Delete from transaction_chart_points where id = :id")
    suspend fun deleteOnlyPoint(id: Long)

    @Query("Select max(to_transaction_id) from transaction_chart_points where chart_id = :chartId")
    suspend fun getHighestTransactionIdForChart(chartId: String): Long?

    @Transaction
    suspend fun deletePoint(pointId: Long, chartId: String) {
        deleteOnlyPoint(pointId)
        deleteValuesForPoint(pointId)
        val chart = getChartByIdSuspend(chartId)
        chart.startAfterTransactionId = getHighestTransactionIdForChart(chartId) ?: 0
        update(chart)
    }

    @Transaction
    suspend fun regenerateValues(pointId: Long) {
        val point = getPointByIdSuspend(pointId)
        deleteValuesForPoint(pointId)
        val chart = getChartByIdSuspend(point.chartId)
        generateValuesForPoint(point, chart, pointId)
    }

    @Query("Select * From transaction_charts where chart_order=:chartOrder")
    suspend fun getChartByOrder(chartOrder: Int): TransactionChart?

    @Transaction
    suspend fun swapChartPosition(a: Int, b: Int) {
        val chartA = getChartByOrder(a)
        val chartB = getChartByOrder(b)
        if(chartA != null && chartB != null) {
            chartA.chartOrder = b
            update(chartA)
            chartB.chartOrder = a
            update(chartB)
        }
    }
}