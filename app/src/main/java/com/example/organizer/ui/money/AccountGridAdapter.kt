package com.example.organizer.ui.money;

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import com.example.organizer.R
import com.example.organizer.database.entity.Account
import com.example.organizer.ui.Utils.ShpaeUtil
import java.util.*

class AccountGridAdapter(val accounts: List<Account>, val activity: FragmentActivity?, val parentView: View) :
    BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        if(position == accounts.size)
            return getViewForAddAccount(position, convertView, parent)
        return getViewForAccount(position, convertView, parent)
    }

    fun getViewForAccount(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(activity, R.layout.account_cell, null);
        val account = accounts[position]
        setAccountCellContainerValues(view, position and 1 == 0)
        setAccountCellContentValues(view, account);
        setTextFieldsForAccount(view, account);
        setAccountSelectListener(view, account.id)
        return view
    }

    fun setAccountSelectListener(view: View, id: String?) {
        view.setOnClickListener { view ->
            run {
                val action = MoneyFragmentDirections.actionNavMoneyToEditAccount(id)
                view.findNavController().navigate(action)
            }
        }
    }

    fun getViewForAddAccount(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(activity, R.layout.add_account_cell, null);
        setAccountCellContainerValues(view, position and 1 == 0)
        setAccountCellContentValues(view, null);
        setAccountSelectListener(view, null)
        return view
    }

    fun setAccountCellContainerValues(view: View, isEven: Boolean) {
        val accountCellContainer = view.findViewById<View>(R.id.accountCellContainer)
        val leftPadding = if (isEven) 10 else 8
        val rightPadding = if (isEven) 8 else 10
        accountCellContainer.setPadding(leftPadding, 6, rightPadding, 6)
    }

    fun setAccountCellContentValues(view: View, account: Account?) {
        val accountCellContent = view.findViewById<View>(R.id.accountCellContent)
        val backgroundColor = if (account == null) Color.WHITE else account.backgroundColor
        val borderColor = if (account == null) view.getResources().getColor(R.color.BlueBCWhiteTC)  else null
        accountCellContent.background = ShpaeUtil.getRoundCornerShape(15.toFloat(),  backgroundColor, borderColor)
    }

    fun setTextFieldsForAccount(view: View, account: Account) {

        var accountLabel = view.findViewById<TextView>(R.id.accountLabel)
        accountLabel.text = account.accountName

        var accountBalance = view.findViewById<TextView>(R.id.accountBalance)
        accountBalance.text = account.unit + " " + account.balance
    }

    override fun getItem(position: Int): Any {
        if(position == accounts.size)
            return Account("Dummy", "addAccount", 0.toDouble(), 0,0, "");
        return accounts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return accounts.size + 1
    }
}
