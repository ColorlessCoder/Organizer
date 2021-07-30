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
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.example.organizer.R
import com.example.organizer.database.AppDatabase
//import com.example.organizer.database.AppDatabase
import com.example.organizer.database.entity.Account
import com.example.organizer.ui.Utils.ColorUtil
import com.example.organizer.ui.money.transactionPlan.EditTemplateTransactionViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class MoneyFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var viewModel: MoneyFragmentViewModel
    val args: MoneyFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_money, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MoneyFragmentViewModel::class.java)
        var accountsGrid: GridView = view.findViewById(R.id.accountsGrid);
        val database = AppDatabase.getInstance(view.context)
        val accountDAO = database.accountDao()
        accountDAO.getAllAccounts().observe(viewLifecycleOwner, Observer { accounts ->
            viewModel.totalAmount =
                accounts.fold(0.0) { acc, account -> account.balance + acc }.toString() + " BDT"
            accountsGrid.adapter =
                AccountGridAdapter(
                    accounts,
                    activity,
                    view
                )
        });
        view.findViewById<View>(R.id.chart_button)
            .setOnClickListener {
                val action = MoneyFragmentDirections.actionNavMoneyToChartList()
                findNavController().navigate(action)
            }
        view.findViewById<View>(R.id.category_card)
            .setOnClickListener {
                val action = MoneyFragmentDirections.actionNavMoneyToTransactionCategory("0")
                findNavController().navigate(action)
            }
        view.findViewById<MaterialButton>(R.id.transactions)
            .setOnClickListener {
                val action = MoneyFragmentDirections.actionNavMoneyToViewTransaction(null);
                findNavController().navigate(action)
            }
        view.findViewById<MaterialButton>(R.id.debts)
            .setOnClickListener {
                val action = MoneyFragmentDirections.actionNavMoneyToDebtList();
                findNavController().navigate(action)
            }
        view.findViewById<MaterialButton>(R.id.total_button)
            .setOnClickListener {
                MaterialAlertDialogBuilder(view.context, R.style.AppTheme_CenterModal)
                    .setTitle("Total balance")
                    .setMessage(viewModel.totalAmount)
                    .show()
            }
        loadTransactionPlanView(view, database)
    }

    private fun loadTransactionPlanView(view: View, database: AppDatabase) {
        view.findViewById<View>(R.id.add_transaction_plan)
            .setOnClickListener {
                val action = MoneyFragmentDirections.actionNavMoneyToEditTransactionPlan(null)
                findNavController().navigate(action)
            }
        val gridview = view.findViewById<GridView>(R.id.transaction_plan_grid)
        database.transactionPlanDao().getAllTransactionPlans().observe(this, Observer {
            gridview.adapter = TransactionPlanGridAdapter(
                it,
                activity,
                view,
                viewModel
            )
        })
        if (viewModel.applyTransactionPlanId.hasObservers()) {
            viewModel.applyTransactionPlanId.removeObservers(this)
        }
        viewModel.applyTransactionPlanId.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                lifecycleScope.launch {
                    try {
                        val successfulMessage = database.transactionDao().applyTransactionPlan(
                            database.templateTransactionDao()
                                .getAllTemplateTransactionDetailsNonLive(it)
                        )
                        MaterialAlertDialogBuilder(view.context)
                            .setTitle("Successfully Applied")
                            .setMessage(successfulMessage)
                            .show()
                    } catch (ex: Exception) {
                        MaterialAlertDialogBuilder(view.context)
                            .setTitle("Problem while Applying")
                            .setMessage(ex.message)
                            .show()
                    } finally {
                        viewModel.applyTransactionPlanId.value = null
                    }
                }
            }
        })
    }

}
