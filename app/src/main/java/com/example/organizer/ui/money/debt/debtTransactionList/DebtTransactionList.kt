package com.example.organizer.ui.money.debt.debtTransactionList

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.ui.money.viewTransaction.ViewTransaction

class DebtTransactionList : Fragment() {

    companion object {
        fun newInstance() = DebtTransactionList()
    }

    private lateinit var viewModel: DebtTransactionListViewModel
    val args: DebtTransactionListArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.debt_transaction_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(DebtTransactionListViewModel::class.java)
        val listView = view.findViewById<RecyclerView>(R.id.transaction_list)
        AppDatabase.getInstance(requireContext()).transactionDao()
            .getAllTransactionsByDebId(args.debtId)
            .observe(this, Observer {
                listView.adapter = ViewTransaction.ViewTransactionListAdapter(
                    it,
                    view,
                    null
                )
            })
        view.findViewById<Button>(R.id.make_payment)
            .setOnClickListener {
                val action = DebtTransactionListDirections.actionDebtTransactionListToDebtPayment(args.debtId)
                findNavController().navigate(action)
            }
    }

}