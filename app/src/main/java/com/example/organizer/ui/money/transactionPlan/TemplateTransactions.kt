package com.example.organizer.ui.money.transactionPlan

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.Enums.TransactionType
import com.example.organizer.database.entity.TemplateTransaction
import com.example.organizer.database.relation.TemplateTransactionDetails
import com.example.organizer.database.relation.TransactionDetails
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.ui.Utils.ShpaeUtil
import com.example.organizer.ui.Utils.StringUtils
import kotlinx.coroutines.launch
import java.util.*

class TemplateTransactions : Fragment() {

    companion object {
        fun newInstance() = TemplateTransactions()
    }

    private lateinit var viewModel: TemplateTransactionsViewModel
    private val args: TemplateTransactionsArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.template_transactions_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(TemplateTransactionsViewModel::class.java)
        viewModel.transacitonPlanId = args.id
        val recyclerView = view.findViewById<RecyclerView>(R.id.template_list)
        val dbInstance = AppDatabase.getInstance(requireContext())
        dbInstance.templateTransactionDao()
            .getAllTemplateTransactionDetails(args.id)
            .observe(this, androidx.lifecycle.Observer {
                recyclerView.adapter = TemplateTransactionListAdapter(
                    it,
                    view,
                    viewModel
                );
                ItemTouchHelper(TemplateTransactionItemTouch(it, viewModel))
                    .attachToRecyclerView(recyclerView);
                viewModel.templates = it
            });

        view.findViewById<View>(R.id.add_template)
            .setOnClickListener {
                val action = TemplateTransactionsDirections.actionTemplateTransactionsToEditTemplateTransaction(viewModel.transacitonPlanId, null, viewModel.templates.size)
                findNavController().navigate(action)
            }
        viewModel.dragStarted.observe(this, androidx.lifecycle.Observer {
            val button = view.findViewById<View>(R.id.save_order)
            if (it) {
                button.visibility = View.VISIBLE
                button.setOnClickListener {
                    saveOrder(dbInstance)
                }
            } else {
                button.visibility = View.GONE
                if (button.hasOnClickListeners()) {
                    button.setOnClickListener(null)
                }
            }
        })
    }

    private fun saveOrder(dbInstance: AppDatabase) {
        val currentTemplateList = viewModel.templates.map { it.transaction }
        val templateListWithChangedOrder = mutableListOf<TemplateTransaction>()
        currentTemplateList.forEachIndexed { index, templateTransaction ->
            if (index + 1 != templateTransaction.order) {
                val clonedTemplateTransaction = templateTransaction.copy()
                clonedTemplateTransaction.order = index + 1
                templateListWithChangedOrder.add(clonedTemplateTransaction)
            }
        }
        lifecycleScope.launch {
            dbInstance.templateTransactionDao().updateList(templateListWithChangedOrder)
            viewModel.dragStarted.value = false
        }
    }

}


class TemplateTransactionListAdapter(
    private val transactionDetailsList: List<TemplateTransactionDetails>,
    val view: View,
    val viewModel: TemplateTransactionsViewModel
) : RecyclerView.Adapter<TemplateTransactionListAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val transactionType: TextView = view.findViewById(R.id.transaction_type)
        val transactionComment: TextView = view.findViewById(R.id.transaction_comment)
        val transactionAmount: TextView = view.findViewById(R.id.transacted_amount)
        val fromAccount: TextView = view.findViewById(R.id.from_account)
        val toAccount: TextView = view.findViewById(R.id.to_account)
        val transactionDate: TextView = view.findViewById(R.id.transaction_date)
        val categoryName: TextView = view.findViewById(R.id.category_name)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.view_transaction_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return transactionDetailsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transactionDetails = transactionDetailsList.get(position)
        val transactionType = TransactionType.from(transactionDetails.transaction.transactionType)
        holder.transactionType.text = transactionType.name
        holder.transactionType.background = ShpaeUtil.getRoundCornerShape(
            15.toFloat(),
            ContextCompat.getColor(view.context, transactionType.color),
            null
        )
        holder.transactionComment.text = transactionDetails.transaction.details
        holder.transactionAmount.text =
            StringUtils.doubleToString(transactionDetails.transaction.amount)
        holder.fromAccount.text = transactionDetails.fromAccountName
        holder.toAccount.text = transactionDetails.toAccountName
        if (transactionDetails.categoryName == null) {
            holder.categoryName.visibility = View.GONE
        } else {
            holder.categoryName.text = transactionDetails.categoryName
            holder.categoryName.background = ShpaeUtil.getRoundCornerShape(
                15.toFloat(),
                Color.WHITE,
                ContextCompat.getColor(view.context, transactionType.color)
            )
        }
        holder.transactionDate.visibility = View.GONE;
        holder.itemView.setOnClickListener {
            if (!viewModel.dragStarted.value!!) {
                val action =
                    TemplateTransactionsDirections.actionTemplateTransactionsToEditTemplateTransaction(
                        viewModel.transacitonPlanId,
                        transactionDetails.transaction.id,
                        transactionDetails.transaction.order
                    )
                view.findNavController().navigate(action)
            }
        }
    }
}

class TemplateTransactionItemTouch(
    private val transactionDetailsList: List<TemplateTransactionDetails>,
    val viewModel: TemplateTransactionsViewModel
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
    0
) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition
        Collections.swap(transactionDetailsList, fromPosition, toPosition)
        recyclerView.adapter!!.notifyItemMoved(fromPosition, toPosition)
        if (fromPosition != toPosition) {
            viewModel.dragStarted.value = true
        }
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) = Unit

}
