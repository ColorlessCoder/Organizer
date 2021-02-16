package com.example.organizer.ui.money.editAccount

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.organizer.MainActivity

import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.dao.AccountDAO
import com.example.organizer.databinding.EditAccountFragmentBinding
import com.example.organizer.ui.Utils.ColorUtil
import com.example.organizer.ui.money.ColorSpinnerAdapter

class EditAccount : Fragment() {

    companion object {
        fun newInstance() =
            EditAccount()
    }

    private lateinit var viewModel: EditAccountViewModel
    private lateinit var accountDAO: AccountDAO
    val args: EditAccountArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("Edit Account: Create View")
        val isCreating = args.id == null
        val actionBarActivity: MainActivity = activity as MainActivity
        actionBarActivity.supportActionBar?.title =
            if (isCreating) "Create Account" else "Edit Account"
        val binding = DataBindingUtil.inflate<EditAccountFragmentBinding>(
            inflater,
            R.layout.edit_account_fragment,
            container,
            false
        );
        val view = binding.root
        viewModel = ViewModelProviders.of(this).get(EditAccountViewModel::class.java)
        binding.editAccountViewModel = viewModel
        val dbInstance = AppDatabase.getInstance(view.context)
        accountDAO = dbInstance.accountDao()
        println(viewModel.accountName.value)
        val colors = ColorUtil.getAllWhiteTextBackgroundColors()
        viewModel.adapter =
            ColorSpinnerAdapter(colors, activity)
        viewModel.accountDAO = accountDAO
        binding.lifecycleOwner = this
        if (!isCreating) {
            updateViewModelWithCurrentAccount(colors)
        }
        viewModel.navigateBack.observe(
            this,
            androidx.lifecycle.Observer { navigateBack ->
                if (navigateBack) this.navigateBackToAccounts(view)
            })
        viewModel.navigateToAddTransaction.observe(this, Observer { navigateToTransactions ->
            if (navigateToTransactions) {
                this.navigateToAddTransactions()
                viewModel.navigateToAddTransaction.value = false;
            }
        })
        return view
    }

    private fun updateViewModelWithCurrentAccount(colors: List<Int>) {
        args.id?.let {
            accountDAO.getAccountById(it).observe(this, androidx.lifecycle.Observer { account ->
                run {
                    viewModel.accountName.value = account.accountName
                    viewModel.amount.value = account.balance.toString()
                    viewModel.backgroundColorIndex.value = colors.indexOf(account.backgroundColor)
                    viewModel.id.value = account.id
                    viewModel.isCreating.value = false
                }
            })
        }
    }

    private fun navigateBackToAccounts(view: View) {
        var action =
            EditAccountDirections.actionEditAccountToNavMoney()
        action.fromCreate = true
        view.findNavController().navigate(action)
    }

    private fun navigateToAddTransactions() {
        if (args.id != null) {
            var action = EditAccountDirections.actionEditAccountToAddTransaction(args.id)
            view?.findNavController()?.navigate(action)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        println("Edit Account: Activity Created")
    }

}
