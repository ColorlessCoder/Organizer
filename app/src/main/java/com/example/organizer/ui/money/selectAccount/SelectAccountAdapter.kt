package com.example.organizer.ui.money.selectAccount

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.entity.Account
import com.example.organizer.ui.Utils.ShpaeUtil
import com.example.organizer.ui.money.common.CommonSelectRecyclerListAdapter
import com.example.organizer.ui.money.common.CommonSelectViewHolder

class SelectAccountAdapter(
    private val accounts: List<Account>,
    private val parentView: View,
    private val viewModel: SelectAccountViewModel
) : CommonSelectRecyclerListAdapter<SelectAccountAdapter.ViewHolder, Account, SelectAccountViewModel>(accounts, viewModel, parentView) {
    class ViewHolder(view: View) : CommonSelectViewHolder(view) {
        val accountLabel: TextView = view.findViewById(R.id.accountLabel)
        val accountBalance: TextView = view.findViewById(R.id.accountBalance)
        val accountCellContent: View = view.findViewById(R.id.accountCellContent)
        override fun getDrawableElement(): TextView {
            return accountLabel
        }
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
        super.onBindViewHolder(holder, position)
    }
}