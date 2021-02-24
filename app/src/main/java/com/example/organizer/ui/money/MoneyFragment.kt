package com.example.organizer.ui.money

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.example.organizer.R
import com.example.organizer.database.AppDatabase
//import com.example.organizer.database.AppDatabase
import com.example.organizer.database.entity.Account
import com.example.organizer.ui.Utils.ColorUtil

class MoneyFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    val args: MoneyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_money, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var accountsGrid: GridView = view.findViewById(R.id.accountsGrid);
        val accountDAO = AppDatabase.getInstance(view.context).accountDao()
        accountDAO.getAllAccounts().observe(this, Observer { accounts ->
            accountsGrid.adapter =
                AccountGridAdapter(
                    accounts,
                    activity,
                    view
                )
        });
        view.findViewById<View>(R.id.category_card)
            .setOnClickListener {
                val action = MoneyFragmentDirections.actionNavMoneyToTransactionCategory()
                findNavController().navigate(action)
            }
    }

}
