package com.example.organizer.ui.money.transactionChart

import android.graphics.Color
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.MenuRes
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.dao.TransactionChartDAO
import com.example.organizer.database.entity.Category
import com.example.organizer.database.entity.TransactionChart
import com.example.organizer.database.entity.TransactionChartPoint
import com.example.organizer.database.entity.TransactionChartValue
import com.example.organizer.database.enums.NoCategoryId
import com.example.organizer.database.enums.TransactionType
import com.example.organizer.ui.money.viewTransaction.ViewTransactionGraphViewModel.Companion.NO_CATEGORY
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*

class TransactionChartViewHolder(
    private val view: View,
    private val parentView: View,
    private val transactionChartDAO: TransactionChartDAO,
    private val lifecycleCoroutineScope: LifecycleCoroutineScope
) : RecyclerView.ViewHolder(view) {
    data class ChartSettings(var showLabel: Boolean, var showValue: Boolean)

    val chart: LineChart = view.findViewById(R.id.line_chart)
    val titleView: TextView = view.findViewById(R.id.chart_title)
    val optionButton: Button = view.findViewById(R.id.chart_options)
    val backChip: Chip = view.findViewById(R.id.back_chip)
    val deepChip: Chip = view.findViewById(R.id.deep_chip)
    var sortedPoints: List<TransactionChartPoint> = mutableListOf()
    private val visiblePoints = MutableLiveData(10L)
    private val nodeTrace = mutableListOf<TreeNode>()
    private val filterTrace = mutableListOf<MutableList<Pair<String, Boolean>>>()
    private var highlightedNode: TreeNode? = null
    private val categoryMap = mutableMapOf<String, Category>()
    private val chartSettings = ChartSettings(showLabel = true, showValue = true)
    private lateinit var transactionChart: TransactionChart
    private val colors = mutableListOf(
        "#e31a1c",
        "#33a02c",
        "#1f78b4",
        "#a6cee3",
        "#b2df8a",
        "#fb9a99",
        "#fdbf6f",
        "#ff7f00",
        "#cab2d6",
        "#6a3d9a",
        "#ffff99",
        "#b15928"
    )
        .map { Color.parseColor(it) }

    private val rootNode: TreeNode = TreeNode(
        NodeValue(-1, "Category", Color.WHITE, mutableMapOf()),
        mutableMapOf()
    )

    data class TreeNode(
        var value: NodeValue,
        var children: MutableMap<String, TreeNode>
    )

    data class NodeValue(
        var level: Int,
        var name: String,
        var color: Int,
        var values: MutableMap<Long, Double>
    )

    private fun insertUpdateCategoryCount(
        currentNode: TreeNode,
        currentCategory: String,
        transactionChartValue: TransactionChartValue?,
        level: Int,
        color: Int
    ) {
        var group = currentCategory.trim()
        val firstGroupKeyIndex = currentCategory.indexOfFirst { it == ':' }
        val groupKeyFound = transactionChart.groupCategories == 1 && firstGroupKeyIndex != -1
        if (groupKeyFound) {
            group = currentCategory.substring(0, firstGroupKeyIndex).trim()
        }
        if (transactionChartValue != null) {
            val value = currentNode.value.values[transactionChartValue.pointId] ?: 0.0
            currentNode.value.values[transactionChartValue.pointId] =
                value + transactionChartValue.value
        }

        if (group.isNotEmpty()) {
            val newCategory =
                if (groupKeyFound) currentCategory.substring(firstGroupKeyIndex + 1)
                    .trim() else ""
            if (!currentNode.children.contains(group)) {
                currentNode.children[group] = TreeNode(
                    NodeValue(
                        level + 1,
                        group,
                        color,
                        mutableMapOf()
                    ),
                    mutableMapOf()
                )
            }
            insertUpdateCategoryCount(
                currentNode.children[group]!!,
                newCategory,
                transactionChartValue,
                level + 1,
                color
            )
        }
    }

    private fun insertChartValueInTree(chartValue: TransactionChartValue) {
        val category = categoryMap[chartValue.entityId]
        if (category != null) {
            insertChartValueInTree(chartValue, category.transactionType, category.categoryName)
        } else {
            try {
                val noCategoryId = NoCategoryId.fromLabel(chartValue.entityId)
                insertChartValueInTree(chartValue, noCategoryId.typeCode, NO_CATEGORY)
            } catch (ex: Exception) {
                print(chartValue)
                println(" ignored")
            }
        }
    }

    private fun getCategoryName(categoryName: String, transactionType: Int): String {
        return (if (transactionChart.groupTransactionType == 1) (TransactionType.from(transactionType).label + ": " ) else "" ) + categoryName
    }

    private fun insertChartValueInTree(
        chartValue: TransactionChartValue?,
        transactionType: Int,
        categoryName: String
    ) {
        insertUpdateCategoryCount(
            rootNode,
            getCategoryName(categoryName, transactionType),
            chartValue,
            -1,
            TransactionType.from(transactionType).color
        )
    }

    private fun initializeCategories(categories: List<Category>) {
//        insertChartValueInTree(null, TransactionType.EXPENSE.typeCode, NO_CATEGORY)
//        insertChartValueInTree(null, TransactionType.TRANSFER.typeCode, NO_CATEGORY)
//        insertChartValueInTree(null, TransactionType.INCOME.typeCode, NO_CATEGORY)
        categories.forEach {
            insertChartValueInTree(null, it.transactionType, it.categoryName)
            categoryMap[it.id] = it
        }
    }

    private fun getRandomColor(): Int {
        val rnd = Random()
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }

    private fun getColor(index: Int): Int {
        if (index >= colors.size) {
            return getRandomColor()
        }
        return colors[index]
    }

    private fun bindDataToChartView(points: List<TransactionChartPoint>) {
        sortedPoints = points.sortedBy(TransactionChartPoint::id)
        if (sortedPoints.isEmpty()) {
            chart.clear()
            return
        }
        val filter = filterTrace.last()
        enableDeepChip(false)
        val currentNode = nodeTrace.last()
        val dataSets = mutableListOf<ILineDataSet>()
        val children = currentNode.children
            .map { it }
            .sortedBy { it.key }
        if (filter.isEmpty()) {
            filter.addAll(children.map { Pair(it.key, true) })
        }

        children
            .forEachIndexed { childIndex, child ->
                val values = mutableListOf<Entry>()
                sortedPoints.forEachIndexed { index, point ->
                    values.add(
                        Entry(
                            index.toFloat(),
                            (child.value.value.values[point.id] ?: 0.0).toFloat()
                        )
                    )
                }
                val dataSet = LineDataSet(values, child.value.value.name)
                dataSet.color = getColor(childIndex)
                dataSet.setCircleColor(dataSet.color)
                dataSet.lineWidth = 2.5f
                dataSet.circleRadius = 4f
                dataSet.isVisible = filter[childIndex].second
                dataSet.isHighlightEnabled = filter[childIndex].second
                dataSets.add(dataSet)
            }
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawBorders(false)
        chart.getAxisRight().isEnabled = false
        chart.getXAxis().setDrawGridLines(false);
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.axisMinimum = -0.25F
        chart.xAxis.mLabelHeight = 20
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(sortedPoints.map { it.label })
        chart.axisLeft.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                val f = DecimalFormat("##.##")
                if (value >= 1000) {
                    return f.format(value / 1000.0) + "K"
                }
                return f.format(value)
            }
        }
        chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
                enableDeepChip(false)
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (h != null) {
                    setHighlightedNode(children[h.dataSetIndex].value)
                }
            }

        })
        chart.extraBottomOffset = 10F
        chart.legend.isWordWrapEnabled = true
        chart.data = LineData(dataSets)
        chart.xAxis.setLabelCount(6, true)
        chart.invalidate()
        chart.animateX(300)
        chart.animate()
    }

    private fun loadData(transactionChart: TransactionChart) {
        this.transactionChart = transactionChart
        lifecycleCoroutineScope.launch {
            initializeCategories(transactionChartDAO.getCategoriesForChart(transactionChart.id))
            val points =
                transactionChartDAO.getPointsForChart(transactionChart.id, visiblePoints.value!!)
            var maxPointId = 0L
            points.forEach { point ->
                maxPointId = Math.max(maxPointId, point.id)
                transactionChartDAO.getValuesForPointInChart(point.id)
                    .forEach {
                        insertChartValueInTree(it)
                    }
            }
            maxPointId++
            if(transactionChart.showExtraOnePoint == 1) {
                val fromId = transactionChart.startAfterTransactionId + 1
                transactionChartDAO.getValuesForExtraPointInChart(maxPointId, fromId, transactionChart.id)
                    .forEach {
                        insertChartValueInTree(it)
                    }
                points.add(TransactionChartPoint(maxPointId, transactionChart.id, transactionChart.extraPointLabel?:"Latest", Date().time, fromId, -1))
            }
            nodeTrace.clear()
            nodeTrace.add(rootNode)
            filterTrace.add(mutableListOf())
            bindDataToChartView(points)
        }
    }

    fun bindViewHolder(transactionChart: TransactionChart) {
        titleView.text = transactionChart.chartName
        loadData(transactionChart)
        enableBackChip(false)
        enableDeepChip(false)
        optionButton.setOnClickListener {
            showMenu(it, R.menu.transaction_chart_menu)
        }
        deepChip.setOnClickListener {
            print("Deep")
            goDeep()
        }
        backChip.setOnClickListener {
            goBack()
        }
    }

    fun goBack() {
        if (nodeTrace.size > 1) {
            nodeTrace.remove(nodeTrace.last())
            filterTrace.remove(filterTrace.last())
            highlightedNode = null
            enableBackChip(nodeTrace.size > 1)
            bindDataToChartView(sortedPoints)
        }
    }

    fun goDeep() {
        println(highlightedNode)
        if (highlightedNode != null) {
            nodeTrace.add(highlightedNode!!)
            filterTrace.add(mutableListOf())
            highlightedNode = null
            enableBackChip(true)
            bindDataToChartView(sortedPoints)
        }
    }

    fun setHighlightedNode(n: TreeNode) {
        highlightedNode = n
        deepChip.text = n.value.name
        enableDeepChip(true)
    }

    fun enableDeepChip(enable: Boolean) {
        deepChip.visibility = if (enable) View.VISIBLE else View.GONE
    }

    fun enableBackChip(enable: Boolean) {
        backChip.visibility = if (enable) View.VISIBLE else View.GONE
    }

    fun invalidateSettingsIntoChart() {

    }

    private fun showMenu(
        v: View,
        @MenuRes menuRes: Int
    ) {

        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)
//        popup.menu.findItem(R.id.label).isChecked = chartSettings.showLabel
//        popup.menu.findItem(R.id.value).isChecked = chartSettings.showValue
        popup.setOnMenuItemClickListener {
            var preventClose = false

            if (it.itemId == R.id.label) {
                it.isChecked = !it.isChecked
                chartSettings.showLabel = !chartSettings.showLabel
                invalidateSettingsIntoChart()
                preventClose = true
            } else if (it.itemId == R.id.value) {
                it.isChecked = !it.isChecked
                chartSettings.showValue = !chartSettings.showValue
                invalidateSettingsIntoChart()
                preventClose = true
            } else if (it.itemId == R.id.edit) {
                val action = ChartListDirections.actionChartListToEditChart(transactionChart.id, 0)
                parentView.findNavController().navigate(action)
            } else if (it.itemId == R.id.clone) {
                val action = ChartListDirections.actionChartListToEditChart(transactionChart.id, 0)
                action.clone = true
                parentView.findNavController().navigate(action)
            } else if (it.itemId == R.id.add_point) {
                val action = ChartListDirections.actionChartListToAddChartPoint(transactionChart.id, 0)
                parentView.findNavController().navigate(action)
            } else if (it.itemId == R.id.point_list) {
                val action = ChartListDirections.actionChartListToTransactionChartPointList(transactionChart.id)
                parentView.findNavController().navigate(action)
            }  else if (it.itemId == R.id.delete) {
                attemptDeleteChart()
            } else if (it.itemId == R.id.filter) {
                showFilters()
            } else if (it.itemId == R.id.move_up) {
                swapChartOrder(transactionChart.chartOrder, transactionChart.chartOrder - 1)
            } else if (it.itemId == R.id.move_down) {
                swapChartOrder(transactionChart.chartOrder, transactionChart.chartOrder + 1)
            }
            if (preventClose) {
                it.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
                it.actionView = View(v.context)
                it.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        return false
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        return false
                    }
                })
            }
            false
        }
        popup.show()
    }

    fun swapChartOrder(a: Int, b: Int) {
        lifecycleCoroutineScope.launch {
            transactionChartDAO.swapChartPosition(a,b)
        }
    }

    fun attemptDeleteChart() {
        MaterialAlertDialogBuilder(parentView.context)
            .setTitle("Delete Chart: ${transactionChart.chartName}")
            .setMessage("Deleting the chart will delete all the points and values. Are you sure?")
            .setPositiveButton("Yes") { _, _->
                lifecycleCoroutineScope.launch {
                    transactionChartDAO.deleteChart(transactionChart.id)
                }
            }
            .setNegativeButton("No") {_, _ -> }
            .show();
    }

    fun setDataSetVisibility() {
        val filter = filterTrace.last()
        chart.data.dataSets.forEachIndexed{ i, d ->
            d.isVisible = filter[i].second
            d.isHighlightEnabled = filter[i].second
        }
        chart.invalidate()
    }

    fun showFilters() {
        val filter = filterTrace.last()
        val clonedOne = filter.map{it.copy()}.toMutableList()
        MaterialAlertDialogBuilder(parentView.context)
            .setTitle("Filter")
            .setPositiveButton("OK") { d, _ ->
                filter.clear()
                filter.addAll(clonedOne)
                setDataSetVisibility()
            }
            .setNegativeButton("Cancel") { d,_ ->
            }
            .setMultiChoiceItems(clonedOne.map { it.first }.toTypedArray(), clonedOne.map{it.second}.toBooleanArray()) { d, w, c ->
                clonedOne[w] = Pair(clonedOne[w].first, c)
            }
            .show()
    }
}