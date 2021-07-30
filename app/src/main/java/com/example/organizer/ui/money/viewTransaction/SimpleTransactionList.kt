package com.example.organizer.ui.money.viewTransaction

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R

class SimpleTransactionList : Fragment() {

    companion object {
        fun newInstance() =
            SimpleTransactionList()
    }

    private lateinit var viewModel: SimpleTransactionListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.simple_transaction_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(SimpleTransactionListViewModel::class.java)
        view.findViewById<RecyclerView>(R.id.transaction_list).adapter =
            ViewTransaction.ViewTransactionListAdapter(viewModel.transactionList.sortedByDescending { it.transaction.transactedAt }, view, null)
    }

}