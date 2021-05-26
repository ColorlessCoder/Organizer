package com.example.organizer.ui.money.transactionChart

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.dao.TransactionChartDAO
import com.example.organizer.database.entity.TransactionChart
import com.example.organizer.database.enums.TransactionType
import com.example.organizer.database.relation.TransactionDetails
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.ui.Utils.ShpaeUtil
import com.example.organizer.ui.Utils.StringUtils
import com.example.organizer.ui.money.viewTransaction.ViewTransactionDirections
import com.example.organizer.ui.money.viewTransaction.ViewTransactionViewModel
import java.util.*

class ChartList : Fragment() {

    companion object {
        fun newInstance() = ChartList()
    }

    private lateinit var viewModel: ChartListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.chart_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChartListViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.create_button).setOnClickListener {
            val action = ChartListDirections.actionChartListToEditChart(null, viewModel.numberOfCharts)
            findNavController().navigate(action)
        }
        val db = AppDatabase.getInstance(requireContext())
        db.transactionChartDao().getAllCharts()
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                viewModel.numberOfCharts = it.size
                view.findViewById<RecyclerView>(R.id.chart_list)
                    .adapter = ChartListAdapter(it, view, db.transactionChartDao(), lifecycleScope)
            })
    }

    class ChartListAdapter(
        private val chartList: List<TransactionChart>,
        val view: View,
        private val transactionChartDAO: TransactionChartDAO,
        private val lifecycleCoroutineScope: LifecycleCoroutineScope
    ) : RecyclerView.Adapter<TransactionChartViewHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): TransactionChartViewHolder {
            return when (viewType) {
                else -> TransactionChartViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.transaction_chart_row, parent, false),
                    view,
                    transactionChartDAO,
                    lifecycleCoroutineScope
                )
            }
        }

        override fun getItemViewType(position: Int): Int {
            return chartList[position].chartType
        }

        override fun getItemCount(): Int {
            return chartList.size
        }

        override fun onBindViewHolder(holder: TransactionChartViewHolder, position: Int) {
            holder.bindViewHolder(chartList[position])
        }

    }
}
