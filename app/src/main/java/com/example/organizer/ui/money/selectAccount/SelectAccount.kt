package com.example.organizer.ui.money.selectAccount

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.MainActivity
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.ui.money.AccountGridAdapter

class SelectAccount : Fragment() {

    companion object {
        fun newInstance() = SelectAccount()
    }

    private lateinit var viewModel: SelectAccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val actionBarActivity: MainActivity = activity as MainActivity
        actionBarActivity.supportActionBar?.title = "Select Account"
        return inflater.inflate(R.layout.select_account_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(SelectAccountViewModel::class.java)
        val accountsList: RecyclerView = view.findViewById(R.id.select_account_list);
        val accountDAO = AppDatabase.getInstance(view.context).accountDao()
        accountDAO.getAllAccounts().observe(this, Observer { accounts ->
            accountsList.adapter =
                SelectAccountAdapter(
                    accounts,
                    view,
                    viewModel
                )
        })
    }

}