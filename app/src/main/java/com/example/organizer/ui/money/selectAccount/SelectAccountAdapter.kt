package com.example.organizer.ui.money.selectAccount

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.setPadding
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.entity.Account

class SelectAccountAdapter(
    private val accounts: List<Account>,
    private val parentView: View,
    private val viewModel: SelectAccountViewModel
) : RecyclerView.Adapter<SelectAccountAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val accountLabel: TextView = view.findViewById(R.id.accountLabel)
        val accountBalance: TextView = view.findViewById(R.id.accountBalance)
        val accountCellContent: View = view.findViewById(R.id.accountCellContent)
        val accountCellContainer: View = view.findViewById(R.id.accountCellContainer)
        init {
            accountCellContainer.setPadding(10,5, 10, 0)
            accountCellContent.setPadding(50,10, 50, 10)
            accountLabel.textSize = 16.0F
            accountBalance.textSize = 11.0F
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.account_cell, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return accounts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val account = accounts.get(position)
        holder.accountLabel.text = account.accountName
        holder.accountBalance.text = account.balance.toString()
        holder.accountCellContent.setBackgroundColor(account.backgroundColor)
        holder.itemView.setOnClickListener {
            viewModel.selectedAccount = account
            parentView.findNavController().popBackStack()
        }
    }
}