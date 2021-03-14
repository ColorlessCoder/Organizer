package com.example.organizer.ui.money.debt.debtlist

import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.entity.Debt
import com.example.organizer.database.enums.DebtType
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.ui.Utils.ShpaeUtil
import com.example.organizer.ui.Utils.StringUtils.Companion.doubleToString
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

class DebtList : Fragment() {

    companion object {
        fun newInstance() = DebtList()
    }

    private lateinit var viewModel: DebtListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.debt_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(DebtListViewModel::class.java)
        view.findViewById<Button>(R.id.create_debt).setOnClickListener {
            findNavController().navigate(DebtListDirections.actionDebtListToEditDebt(null))
        }
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val db = AppDatabase.getInstance(requireContext())
        viewModel.debtDAO = db.debtDao()
        viewModel.debtDAO.getAllIncompleteDebts()
            .observe(this, androidx.lifecycle.Observer {
                recyclerView.adapter = DebtListAdapter(
                    it,
                    view,
                    viewModel
                )
            })
    }

    class DebtListAdapter(
        private val debtList: List<Debt>,
        val view: View,
        val viewModel: DebtListViewModel
    ) : RecyclerView.Adapter<DebtListAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val debtType: TextView = view.findViewById(R.id.debt_type)
            val dueDate: TextView = view.findViewById(R.id.due_date)
            val debtDetails: TextView = view.findViewById(R.id.debt_details)
            val remainingDebtAmount: TextView = view.findViewById(R.id.remaining_debt_amount)
            val createdAt: TextView = view.findViewById(R.id.created_at)

            val card: MaterialCardView = view.findViewById(R.id.debt_row_card)
            val actionBar: ConstraintLayout = view.findViewById(R.id.debt_action_bar)
            val paymentButton: Button = view.findViewById(R.id.payment_button)
            val historyButton: Button = view.findViewById(R.id.history_button)
            val editButton: Button = view.findViewById(R.id.edit_button)
            val completeButton: Button = view.findViewById(R.id.complete_button)
            val deleteButton: Button = view.findViewById(R.id.delete_button)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.debt_row, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return debtList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val debt = debtList[position]
            val debtType = DebtType.from(debt.debtType)
            holder.debtType.text = debtType.name
            holder.debtType.background = ShpaeUtil.getRoundCornerShape(
                15.toFloat(),
                ContextCompat.getColor(view.context, debtType.color),
                null
            )
            if (debt.completedAt != null) {
                holder.dueDate.text =
                    "Completed " + DateUtils.dateToString(Date(debt.completedAt!!))
            } else if (debt.scheduledAt != null) {
                holder.dueDate.text = "Due " + DateUtils.dateToString(Date(debt.scheduledAt!!))
            }
            if (debt.completedAt != null) {
                holder.remainingDebtAmount.text = doubleToString(debt.paidSoFar)
            } else {
                holder.remainingDebtAmount.text = doubleToString(debt.amount - debt.paidSoFar)
            }
            holder.createdAt.text = DateUtils.dateToString(Date(debt.createdAt))
            holder.debtDetails.text = debt.details

            holder.card.setOnFocusChangeListener { _, hasFocus ->
                holder.actionBar.visibility = if (hasFocus) View.VISIBLE else View.GONE
            }
            holder.historyButton.setOnClickListener {
                view.findNavController()
                    .navigate(DebtListDirections.actionDebtListToDebtTransactionList(debt.id))
            }
            holder.deleteButton.setOnClickListener {
                MaterialAlertDialogBuilder(view.context)
                    .setMessage("Do you want to delete the debt?")
                    .setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
                        viewModel.deleteDebt(debt.id)
                        dialogInterface.dismiss()
                    }
                    .setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                    }
                    .show()
            }
            if (debt.completedAt == null) {
                holder.editButton.setOnClickListener {
                    view.findNavController()
                        .navigate(DebtListDirections.actionDebtListToEditDebt(debt.id))
                }
                holder.paymentButton.setOnClickListener {
                    view.findNavController()
                        .navigate(DebtListDirections.actionDebtListToDebtPayment(debt.id))
                }
                holder.completeButton.setOnClickListener {
                    MaterialAlertDialogBuilder(view.context)
                        .setMessage("Do you want to complete the debt?")
                        .setPositiveButton("Yes") { dialogInterface: DialogInterface, _: Int ->
                            viewModel.completeDebt(debt)
                            dialogInterface.dismiss()
                        }
                        .setNegativeButton("No") { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                        }
                        .show()
                }
            } else {
                holder.editButton.visibility = View.GONE
                holder.paymentButton.visibility = View.GONE
                holder.completeButton.visibility = View.GONE
            }
        }

    }
}