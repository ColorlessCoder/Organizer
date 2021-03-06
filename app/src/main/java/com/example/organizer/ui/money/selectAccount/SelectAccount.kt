package com.example.organizer.ui.money.selectAccount

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.MainActivity
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.entity.Account
import com.example.organizer.ui.money.common.CommonSelectFragment

class SelectAccount : CommonSelectFragment<Account, SelectAccountViewModel, SelectAccountAdapter.ViewHolder, SelectAccountAdapter>() {

    companion object {
        fun newInstance() = SelectAccount()
    }

    private lateinit var currentView: View;

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
        currentView = view
        selectViewModel = ViewModelProvider(requireActivity()).get(SelectAccountViewModel::class.java)
        val accountDAO = AppDatabase.getInstance(view.context).accountDao()
        accountDAO.getAllAccounts().observe(this, Observer { accounts ->
            setSelectGridAdapter(
                accounts,
                SelectAccountAdapter(
                    accounts,
                    view,
                    selectViewModel
                )
            )
        })
        handleCommonSelectButtons(view)
    }

    override fun getSelectRecyclerView(): RecyclerView {
        return currentView.findViewById(R.id.select_account_list)
    }

}