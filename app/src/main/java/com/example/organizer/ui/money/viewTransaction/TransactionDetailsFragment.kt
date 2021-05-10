package com.example.organizer.ui.money.viewTransaction

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.dao.TransactionDAO
import com.example.organizer.database.enums.TransactionType
import com.example.organizer.database.relation.TransactionDetails
import com.example.organizer.ui.Utils.StringUtils
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class TransactionDetailsFragment : Fragment() {
    companion object {
        fun newInstance() =
            TransactionDetailsFragment()
    }

    private lateinit var transactionDAO: TransactionDAO
    val args: TransactionDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        transactionDAO = AppDatabase.getInstance(requireContext()).transactionDao()
        transactionDAO.getTransactionDetailsById(args.transactionId)
            .observe(viewLifecycleOwner, Observer {
                if (it != null)
                    updateView(view, it)
            })
    }

    private fun updateView(view: View, transaction: TransactionDetails) {
        view.findViewById<TextView>(R.id.transactionTypeLabel).text =
            TransactionType.from(transaction.transaction.transactionType).name
        view.findViewById<TextView>(R.id.amountText).text =
            StringUtils.doubleToString(transaction.transaction.amount)
        if (transaction.transaction.fromAccount != null) {
            view.findViewById<LinearLayout>(R.id.from_account_section).visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.from_account_name).text = transaction.fromAccountName
            view.findViewById<TextView>(R.id.from_account_old).text =
                StringUtils.doubleToString(transaction.transaction.fromAccountOldAmount)
            view.findViewById<TextView>(R.id.from_account_new).text =
                StringUtils.doubleToString(transaction.transaction.fromAccountNewAmount)
        } else {
            view.findViewById<LinearLayout>(R.id.from_account_section).visibility = View.GONE
        }

        if (transaction.transaction.toAccount != null) {
            view.findViewById<LinearLayout>(R.id.to_account_section).visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.to_account_name).text = transaction.toAccountName
            view.findViewById<TextView>(R.id.to_account_old).text =
                StringUtils.doubleToString(transaction.transaction.toAccountOldAmount)
            view.findViewById<TextView>(R.id.to_account_new).text =
                StringUtils.doubleToString(transaction.transaction.toAccountNewAmount)
        } else {
            view.findViewById<LinearLayout>(R.id.to_account_section).visibility = View.GONE
        }

        if (transaction.transaction.transactionCategoryId != null) {
            view.findViewById<LinearLayout>(R.id.category_section).visibility = View.VISIBLE
            view.findViewById<TextView>(R.id.category_name).text = transaction.categoryName
        } else {
            view.findViewById<LinearLayout>(R.id.category_section).visibility = View.GONE
        }
        view.findViewById<TextView>(R.id.comments).text = transaction.transaction.details
        val revertButton = view.findViewById<MaterialButton>(R.id.revert_button)
        if (transaction.transaction.debtId == null) {
            revertButton.isEnabled = true
            revertButton.setOnClickListener {
                MaterialAlertDialogBuilder(view.context)
                    .setTitle("Are you sure?")
                    .setMessage("Reverting the transaction will delete this transaction forever and add/subtract the amount to accounts.")
                    .setPositiveButton("Revert") { dialogInterface: DialogInterface, _: Int ->
                        lifecycleScope.launch {
                            transactionDAO.revertTransaction(transaction.transaction)
                            dialogInterface.dismiss()
                            findNavController().popBackStack()
                        }
                    }
                    .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                    }
                    .show()
            }
        } else {
            revertButton.isEnabled = false
        }
    }
}