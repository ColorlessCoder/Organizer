package com.example.organizer.ui.money.selectTransactionType

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.Enums.TransactionType
import com.example.organizer.ui.Utils.ShpaeUtil

class SelectTransactionType : Fragment() {

    companion object {
        fun newInstance() = SelectTransactionType()
    }

    private lateinit var viewModel: SelectTransactionTypeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.select_transaction_type_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(SelectTransactionTypeViewModel::class.java)
        view.findViewById<RecyclerView>(R.id.selectTransactionTypeList)
            .adapter = TransactionTypeListAdapter(
            TransactionType.values().asList(),
            viewModel,
            view
        )
    }


    class TransactionTypeListAdapter(
        private val transactionTypeList: List<TransactionType>,
        private val viewModel: SelectTransactionTypeViewModel,
        private val parentView: View
    ) : RecyclerView.Adapter<TransactionTypeListAdapter.ViewHolder>() {
        lateinit var context: Context

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val label: TextView = view.findViewById(R.id.transactionTypeLabel)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            context = parent.context
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.transaction_type_row, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return transactionTypeList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val type = transactionTypeList.get(position)
            holder.label.text = type.name
            holder.label.background = ShpaeUtil.getRoundCornerShape(15.toFloat(), ContextCompat.getColor(context, type.color), null)
            holder.itemView.setOnClickListener {
                viewModel.transactionType = type
                parentView.findNavController().popBackStack()
            }
        }
    }
}