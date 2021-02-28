package com.example.organizer.ui.money;

import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.organizer.R
import com.example.organizer.database.entity.TransactionPlan
import com.example.organizer.ui.Utils.ShpaeUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class TransactionPlanGridAdapter(
    private val transactionPlanList: List<TransactionPlan>,
    val activity: FragmentActivity?,
    private val parentView: View,
    private val viewModel: MoneyFragmentViewModel
) :
    BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return getViewForPlan(position, convertView, parent)
    }

    private fun getViewForPlan(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(activity, R.layout.account_cell, null);
        val transactionPlan = transactionPlanList[position]
        setAccountCellContainerValues(view, position and 1 == 0)
        setAccountCellContentValues(view, transactionPlan);
        setTextFieldsForPlan(view, transactionPlan);
        setSelectListener(view, transactionPlan.id)
        return view
    }

    private fun setSelectListener(view: View, id: String) {
        view.setOnClickListener { view ->
            run {
                val items = arrayOf("Edit Plan Details", "Template Transactions", "Apply Plan")
                MaterialAlertDialogBuilder(view.context, R.style.AppTheme_TransactionPlanAction)
                    .setItems(items) { dialog, which ->
                        if(which == 0) {
                            val action = MoneyFragmentDirections.actionNavMoneyToEditTransactionPlan(id)
                            dialog.dismiss()
                            parentView.findNavController().navigate(action)
                        } else if (which == 1) {
                            val action = MoneyFragmentDirections.actionNavMoneyToTemplateTransactions(id)
                            dialog.dismiss()
                            parentView.findNavController().navigate(action)
                        } else if (which == 2) {
                            viewModel.applyTransactionPlanId.value = id
                        }
                    }
                    .show()
            }
        }
    }

    private fun setAccountCellContainerValues(view: View, isEven: Boolean) {
        val accountCellContainer = view.findViewById<View>(R.id.accountCellContainer)
        val leftPadding = if (isEven) 10 else 8
        val rightPadding = if (isEven) 8 else 10
        accountCellContainer.setPadding(leftPadding, 6, rightPadding, 6)
    }

    private fun setAccountCellContentValues(view: View, transactionPlan: TransactionPlan) {
        val accountCellContent = view.findViewById<View>(R.id.accountCellContent)
        val backgroundColor = Color.WHITE
        val borderColor = transactionPlan.color
        accountCellContent.background =
            ShpaeUtil.getRoundCornerShape(15.toFloat(), backgroundColor, borderColor)
    }

    private fun setTextFieldsForPlan(view: View, transactionPlan: TransactionPlan) {

        val accountLabel = view.findViewById<TextView>(R.id.accountLabel)
        accountLabel.text = transactionPlan.name
        accountLabel.setTextColor(transactionPlan.color)
        accountLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16F);
        accountLabel.setPadding(25,5,0,5)

        val accountBalance = view.findViewById<TextView>(R.id.accountBalance)
        accountBalance.visibility = View.GONE
    }

    override fun getItem(position: Int): Any {
        return transactionPlanList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return transactionPlanList.size
    }
}