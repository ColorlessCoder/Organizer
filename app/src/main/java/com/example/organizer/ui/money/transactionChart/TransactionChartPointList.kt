package com.example.organizer.ui.money.transactionChart

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.dao.TransactionChartDAO
import com.example.organizer.database.entity.TransactionChart
import com.example.organizer.database.entity.TransactionChartPoint
import kotlinx.coroutines.launch

class TransactionChartPointList : Fragment() {

    companion object {
        fun newInstance() = TransactionChartPointList()
    }

    private lateinit var viewModel: TransactionChartPointListViewModel
    private val args: TransactionChartPointListArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.transaction_chart_point_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(TransactionChartPointListViewModel::class.java)
        val db = AppDatabase.getInstance(requireContext())
        viewModel.viewModelScope.launch {
            view.findViewById<RecyclerView>(R.id.point_list).adapter = ListAdapter(
                db.transactionChartDao().getAllPointsForChart(args.chartId),
                view
            )
        }
    }

    class ListAdapter(
        private val list: List<TransactionChartPoint>,
        val view: View
    ) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val order: TextView = view.findViewById(R.id.order)
            val label: TextView = view.findViewById(R.id.label)
            val from: TextView = view.findViewById(R.id.from)
            val to: TextView = view.findViewById(R.id.to)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.point_row, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return list.size + 1
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (position == 0) {
                holder.order.text = "Serial"
                holder.from.text = "From"
                holder.to.text = "To"
                holder.label.text = "Label"
            } else {
                val r = list[position - 1]
                holder.order.text = (itemCount - position).toString()
                holder.from.text = r.fromTransactionId.toString()
                holder.to.text = r.toTransactionId.toString()
                holder.label.text = r.label
                holder.itemView.setOnClickListener {
                    val action = TransactionChartPointListDirections.actionTransactionChartPointListToAddChartPoint(r.chartId, r.id)
                    action.deletedEnabled = position == 1
                    view.findNavController().navigate(action)
                }
            }
        }


    }
}