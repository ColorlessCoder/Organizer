package com.example.organizer.ui.money.viewTransaction

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.MainActivity
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.Enums.TransactionType
import com.example.organizer.database.relation.TransactionDetails
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.ui.Utils.ShpaeUtil
import com.example.organizer.ui.Utils.StringUtils
import com.example.organizer.ui.money.selectAccount.SelectAccountAdapter
import com.example.organizer.ui.money.selectAccount.SelectAccountViewModel
import java.util.*

class ViewTransaction : Fragment() {

    companion object {
        fun newInstance() = ViewTransaction()
    }

    private lateinit var viewModel: ViewTransactionViewModel
    val args: ViewTransactionArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val actionBarActivity: MainActivity = activity as MainActivity
        actionBarActivity.supportActionBar?.title = "Transactions"
        return inflater.inflate(R.layout.view_transaction_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ViewTransactionViewModel::class.java)
        val transactionList: RecyclerView = view.findViewById(R.id.view_transaction_list);
        val transactionDAO = AppDatabase.getInstance(view.context).transactionDao()
        var transactionDetailsList = if (args.sourceAccountId != null) transactionDAO.getAllTransactionDetails(
            args.sourceAccountId!!
        ) else transactionDAO.getAllTransactionDetails();
        transactionDetailsList.observe(this, Observer { transactions ->
            transactionList.adapter =
                ViewTransactionListAdapter(
                    transactions,
                    view
                )
        })
    }

    class ViewTransactionListAdapter(private val transactionDetailsList: List<TransactionDetails>, val view: View): RecyclerView.Adapter<ViewTransactionListAdapter.ViewHolder>() {
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
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_transaction_row, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return transactionDetailsList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val transactionDetails = transactionDetailsList.get(position)
            val transactionType = TransactionType.from(transactionDetails.transaction.transactionType)
            holder.transactionType.text = transactionType.name
            holder.transactionType.background = ShpaeUtil.getRoundCornerShape(15.toFloat(),  ContextCompat.getColor(view.context, transactionType.color), null)
            holder.transactionComment.text = transactionDetails.transaction.details
            holder.transactionAmount.text = StringUtils.doubleToString(transactionDetails.transaction.amount)
            holder.fromAccount.text = transactionDetails.fromAccountName
            holder.toAccount.text = transactionDetails.toAccountName
            if(transactionDetails.categoryName == null) {
                holder.categoryName.visibility = View.GONE
            } else {
                holder.categoryName.text = transactionDetails.categoryName
                holder.categoryName.background = ShpaeUtil.getRoundCornerShape(15.toFloat(),  Color.WHITE, ContextCompat.getColor(view.context, transactionType.color))
            }
            holder.transactionDate.text = DateUtils.dateToString(Date(transactionDetails.transaction.transactedAt));
        }

    }

}