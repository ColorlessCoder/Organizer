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
import com.example.organizer.ui.Utils.ShpaeUtil

class SelectAccountAdapter(
    private val accounts: List<Account>,
    private val parentView: View,
    private val viewModel: SelectAccountViewModel
) : RecyclerView.Adapter<SelectAccountAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val accountLabel: TextView = view.findViewById(R.id.accountLabel)
        val accountBalance: TextView = view.findViewById(R.id.accountBalance)
        val accountCellContent: View = view.findViewById(R.id.accountCellContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.account_cell_for_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return accounts.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val account = accounts.get(position)
        holder.accountLabel.text = account.accountName
        holder.accountBalance.text = account.balance.toString()
        holder.accountCellContent.background = ShpaeUtil.getRoundCornerShape(15.toFloat(),  account.backgroundColor, null)
        holder.itemView.setOnClickListener {
            viewModel.selectedAccount = account
            parentView.findNavController().popBackStack()
        }
    }
}