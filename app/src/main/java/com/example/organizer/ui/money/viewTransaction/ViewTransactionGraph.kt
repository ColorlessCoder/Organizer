package com.example.organizer.ui.money.viewTransaction

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.OnBackPressedCallback
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.organizer.R
import com.example.organizer.database.enums.DebtType
import com.example.organizer.database.enums.TransactionType
import com.example.organizer.database.relation.TransactionDetails
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.button.MaterialButton


class ViewTransactionGraph : Fragment() {

    companion object {
        fun newInstance() = ViewTransactionGraph()
        abstract class ChartMenuEventListener() {
            abstract fun onShowLabelChanged(value: Boolean)
            abstract fun onShowValueChanged(value: Boolean)
        }
    }

    private lateinit var viewModel: ViewTransactionGraphViewModel
    private lateinit var simpleTransactionListViewModel: SimpleTransactionListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.view_transaction_graph_fragment, container, false)
    }

    private fun insertUpdateCategorySummary(
        currentNode: ViewTransactionSummary.CategoryGroupTreeNode,
        currentCategory: CategorySummaryRowData,
        transaction: TransactionDetails,
        level: Int
    ) {
        var group = currentCategory.name.trim()
        val firstGroupKeyIndex = currentCategory.name.indexOfFirst { it == ':' }
        if (firstGroupKeyIndex != -1) {
            group = currentCategory.name.substring(0, firstGroupKeyIndex).trim()
        }
        currentNode.value.total += currentCategory.total
        currentNode.value.numberOfTransactions++
        currentNode.value.leaf = currentNode.value.leaf && group.isEmpty()

        if (group.isNotEmpty()) {
            currentCategory.name =
                if (firstGroupKeyIndex != -1) currentCategory.name.substring(firstGroupKeyIndex + 1)
                    .trim() else ""
            if (!currentNode.children.contains(group)) {
                currentNode.children[group] = ViewTransactionSummary.CategoryGroupTreeNode(
                    CategorySummaryRowData(
                        level + 1,
                        group,
                        0.0,
                        currentCategory.color,
                        0,
                        mutableSetOf(),
                        true,
                        mutableListOf()
                    ),
                    mutableMapOf()
                )
            }
            insertUpdateCategorySummary(
                currentNode.children[group]!!,
                currentCategory,
                transaction,
                level + 1
            )
        } else {
            currentNode.value.transactions.add(transaction)
        }
    }

    private fun createBlankSummaryRowData(
        name: String?,
        amount: Double,
        color: Int
    ): CategorySummaryRowData {
        return CategorySummaryRowData(
            0,
            name ?: "",
            amount,
            color,
            0,
            mutableSetOf(),
            true,
            mutableListOf()
        )
    }

    private fun getCategoryWiseSummary(): ViewTransactionSummary.CategoryGroupTreeNode {
        val incomeColor = ContextCompat.getColor(requireContext(), TransactionType.INCOME.color)
        val expenseColor = ContextCompat.getColor(requireContext(), TransactionType.EXPENSE.color)
        val transferColor = ContextCompat.getColor(requireContext(), TransactionType.TRANSFER.color)
        val rootCategory = ViewTransactionSummary.CategoryGroupTreeNode(
            createBlankSummaryRowData("Category", 0.0, Color.WHITE),
            mutableMapOf()
        )
        viewModel.getTransactions()
            .filter { it.debt == null }
            .forEach {
                val transaction = it.transaction
                val categoryName = it.categoryName ?: ViewTransactionGraphViewModel.NO_CATEGORY
                if (it.transaction.transactionType == TransactionType.EXPENSE.typeCode) {
                    insertUpdateCategorySummary(
                        rootCategory,
                        createBlankSummaryRowData(
                            ViewTransactionGraphViewModel.EXPENSE + ": " + categoryName,
                            transaction.amount,
                            expenseColor
                        ),
                        it,
                        -1
                    )
                } else if (it.transaction.transactionType == TransactionType.INCOME.typeCode) {
                    insertUpdateCategorySummary(
                        rootCategory,
                        createBlankSummaryRowData(
                            ViewTransactionGraphViewModel.INCOME + ": " + categoryName,
                            transaction.amount,
                            incomeColor
                        ),
                        it,
                        -1
                    )
                } else if (it.transaction.transactionType == TransactionType.TRANSFER.typeCode) {
                    insertUpdateCategorySummary(
                        rootCategory,
                        createBlankSummaryRowData(
                            ViewTransactionGraphViewModel.TRANSFER + ": " + categoryName,
                            transaction.amount,
                            transferColor
                        ),
                        it,
                        -1
                    )
                }
            }
        return rootCategory
    }

    fun setCategoryPieChart(view: View) {
        val currentNode = viewModel.categoryTrace.last()
        val pieChart = view.findViewById<PieChart>(R.id.category_pie_chart)
        val categories = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()
        currentNode.children.forEach {
            val pieEntry = PieEntry(it.value.value.total.toFloat(), it.key)
            pieEntry.x = categories.size.toFloat()
            categories.add(pieEntry)
            if (viewModel.categoryTrace.size == 1) {
                colors.add(it.value.value.color)
            }
        }
        val pieDataSet = PieDataSet(categories, "")
        colors.addAll(ColorTemplate.COLORFUL_COLORS.toList())
        colors.addAll(ColorTemplate.JOYFUL_COLORS.toList())
        colors.addAll(ColorTemplate.MATERIAL_COLORS.toList())
        colors.addAll(ColorTemplate.LIBERTY_COLORS.toList())
        colors.addAll(ColorTemplate.VORDIPLOM_COLORS.toList())
        colors.addAll(ColorTemplate.PASTEL_COLORS.toList())
        pieDataSet.colors = colors
        pieDataSet.valueTextColor = Color.BLACK
        pieDataSet.valueTextSize = 16F
        pieDataSet.setDrawValues(viewModel.pieSettings.showValue)

        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.centerText = if (viewModel.categoryTrace.size > 2) viewModel.categoryTrace.subList(
            2,
            viewModel.categoryTrace.size
        ).joinToString(":") { it.value.name } else currentNode.value.name
        pieChart.setHoleColor(currentNode.value.color)
        pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    val child = currentNode.children[categories[e.x.toInt()].label]
                    if (child != null) {
                        if (viewModel.pieSettings.navigateDeep && child.children.isNotEmpty()) {
                            viewModel.categoryTrace.add(child)
                            setAllCharts(view)
                        } else if(viewModel.pieSettings.navigateToList) {
                            navigateToTransactionList(child)
                        }
                    }
                }
            }

        })
        pieChart.notifyDataSetChanged()
        pieChart.setDrawEntryLabels(viewModel.pieSettings.showLabel)
        pieChart.elevation = 5.0.toFloat()
        pieChart.invalidate()
        pieChart.animate()
    }

    fun setCategoryBarChart(view: View) {
        val currentNode = viewModel.categoryTrace.last()
        val barChart = view.findViewById<BarChart>(R.id.category_bar_chart)
        val categories = mutableListOf<BarEntry>()
        val colors = mutableListOf<Int>()
        val labels = mutableListOf<String>()
        currentNode.children.forEach {
            val barEntry = BarEntry(categories.size.toFloat(), it.value.value.total.toFloat())
            labels.add(it.key)
            categories.add(barEntry)
            if (viewModel.categoryTrace.size == 1) {
                colors.add(it.value.value.color)
            }
        }
        val barDataSet = BarDataSet(categories,
            if (viewModel.categoryTrace.size > 2) viewModel.categoryTrace.subList(
                2,
                viewModel.categoryTrace.size
            ).joinToString(":") { it.value.name } else currentNode.value.name)
        colors.addAll(ColorTemplate.COLORFUL_COLORS.toList())
        colors.addAll(ColorTemplate.JOYFUL_COLORS.toList())
        colors.addAll(ColorTemplate.MATERIAL_COLORS.toList())
        colors.addAll(ColorTemplate.LIBERTY_COLORS.toList())
        colors.addAll(ColorTemplate.VORDIPLOM_COLORS.toList())
        colors.addAll(ColorTemplate.PASTEL_COLORS.toList())
        barDataSet.colors = colors
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.valueTextSize = 16F

        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onNothingSelected() {
            }

            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e != null) {
                    val child = currentNode.children[labels[e.x.toInt()]]
                    if (child != null) {
                        if (viewModel.barSettings.navigateDeep && child.children.isNotEmpty()) {
                            viewModel.categoryTrace.add(child)
                            setAllCharts(view)
                        } else if(viewModel.barSettings.navigateToList) {
                            navigateToTransactionList(child)
                        }
                    }
                }
            }

        })
        barChart.notifyDataSetChanged()
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        barChart.xAxis.setDrawGridLines(false)
        barChart.invalidate()
        barChart.animate()
    }

    fun setAllCharts(view: View) {
        setCategoryPieChart(view)
        setCategoryBarChart(view)
    }

    fun navigateToTransactionList(node: ViewTransactionSummary.CategoryGroupTreeNode) {
        simpleTransactionListViewModel.transactionList.clear()
        viewModel.getAllTransactionsUnderCategory(node, simpleTransactionListViewModel.transactionList)
        val action = ViewTransactionGraphDirections.actionViewTransactionGraphToSimpleTransactionList()
        findNavController().navigate(action)
    }

    fun setChartSettings(view: View) {
        val pieSettings = view.findViewById<MaterialButton>(R.id.pie_chart_setting)
        pieSettings.setOnClickListener {
            showMenu(it, R.menu.pie_chart_menu, viewModel.pieSettings, object:
                ChartMenuEventListener() {
                override fun onShowLabelChanged(value: Boolean) {
                    val pieChart = view.findViewById<PieChart>(R.id.category_pie_chart)
                    pieChart.setDrawEntryLabels(value)
                    pieChart.notifyDataSetChanged()
                    pieChart.invalidate()
                }

                override fun onShowValueChanged(value: Boolean) {
                    val pieChart = view.findViewById<PieChart>(R.id.category_pie_chart)
                    pieChart.data.dataSet.setDrawValues(value)
                    pieChart.notifyDataSetChanged()
                    pieChart.invalidate()
                }

            })
        }
        val barSettings = view.findViewById<MaterialButton>(R.id.bar_chart_setting)
        barSettings.setOnClickListener {
            showMenu(it, R.menu.pie_chart_menu, viewModel.barSettings, object:
                ChartMenuEventListener() {
                override fun onShowLabelChanged(value: Boolean) {
                    val barChart = view.findViewById<BarChart>(R.id.category_bar_chart)
                    barChart.xAxis.setDrawLabels(value)
                    barChart.notifyDataSetChanged()
                    barChart.invalidate()
                }

                override fun onShowValueChanged(value: Boolean) {
                    val barChart = view.findViewById<BarChart>(R.id.category_bar_chart)
                    barChart.barData.setDrawValues(value)
                    barChart.notifyDataSetChanged()
                    barChart.invalidate()
                }

            })
        }
    }

    private fun showMenu(
        v: View,
        @MenuRes menuRes: Int,
        settings: ViewTransactionGraphViewModel.Companion.ChartSettings,
        eventListener: ChartMenuEventListener
    ) {

        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.menu.findItem(R.id.label).isChecked = settings.showLabel
        popup.menu.findItem(R.id.value).isChecked = settings.showValue
        popup.menu.findItem(R.id.deep).isChecked = settings.navigateDeep
        popup.menu.findItem(R.id.list).isChecked = settings.navigateToList
        popup.setOnMenuItemClickListener {
            var preventClose = true

            if (it.itemId == R.id.label) {
                it.isChecked = !it.isChecked
                settings.showLabel = !settings.showLabel
                eventListener.onShowLabelChanged(it.isChecked)
            } else if (it.itemId == R.id.value) {
                it.isChecked = !it.isChecked
                settings.showValue = !settings.showValue
                eventListener.onShowValueChanged(it.isChecked)
            } else if (it.itemId == R.id.deep) {
                settings.navigateDeep = !settings.navigateDeep
                it.isChecked = !it.isChecked
                if (settings.navigateDeep && settings.navigateToList) {
                    popup.menu.findItem(R.id.list).isChecked = false
                    settings.navigateToList = false
                }
            } else if (it.itemId == R.id.list) {
                settings.navigateToList = !settings.navigateToList
                it.isChecked = !it.isChecked
                if (settings.navigateDeep && settings.navigateToList) {
                    popup.menu.findItem(R.id.deep).isChecked = false
                    settings.navigateDeep = false
                }
            } else {
                preventClose = false
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(requireActivity()).get(ViewTransactionGraphViewModel::class.java)
        simpleTransactionListViewModel =
            ViewModelProvider(requireActivity()).get(SimpleTransactionListViewModel::class.java)
        if (viewModel.categoryTrace.isEmpty()) {
            viewModel.categoryTrace.add(getCategoryWiseSummary())
        }
        setChartSettings(view)
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.categoryTrace.size < 2) {
                    findNavController().popBackStack()
                } else {
                    viewModel.categoryTrace.remove(viewModel.categoryTrace.last())
                    setAllCharts(view)
                }
            }
        })
        setAllCharts(view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        activity?.onBackPressedDispatcher?.
    }
}