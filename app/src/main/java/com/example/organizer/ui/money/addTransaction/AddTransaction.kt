package com.example.organizer.ui.money.addTransaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.organizer.MainActivity
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.services.TransactionsService
import com.example.organizer.databinding.AddTransactionFragmentBinding
import com.example.organizer.ui.money.common.CommonSelectViewModel
import com.example.organizer.ui.money.selectAccount.SelectAccountViewModel
import com.example.organizer.ui.money.transactionCategory.SelectCategoryViewModel
import com.google.android.material.textfield.TextInputLayout

class AddTransaction : Fragment() {

    companion object {
        fun newInstance() = AddTransaction()
    }

    private lateinit var viewModel: AddTransactionViewModel
    private lateinit var selectAccountViewModel: SelectAccountViewModel
    private lateinit var selectCategoryViewModel: SelectCategoryViewModel
    val args: AddTransactionArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val actionBarActivity: MainActivity = activity as MainActivity
        actionBarActivity.supportActionBar?.title = "Create Transaction"
        val binding = DataBindingUtil.inflate<AddTransactionFragmentBinding>(
            inflater,
            R.layout.add_transaction_fragment,
            container,
            false
        );
        val view = binding.root
        viewModel = ViewModelProviders.of(this).get(AddTransactionViewModel::class.java)
        val dbInstance = AppDatabase.getInstance(requireContext())
        viewModel.transactionService = TransactionsService(dbInstance.transactionDao())
        selectAccountViewModel =
            ViewModelProviders.of(requireActivity()).get(SelectAccountViewModel::class.java)
        selectCategoryViewModel =
            ViewModelProviders.of(requireActivity()).get(SelectCategoryViewModel::class.java)
        binding.addTransactionViewModel = viewModel
        binding.lifecycleOwner = this
        if (viewModel.fieldPendingToSetAfterNavigateBack == AddTransactionViewModel.Companion.FIELDS.FROM_ACCOUNT) {
            if (selectAccountViewModel.selectedRecord != null) {
                viewModel.fromAccount.value = selectAccountViewModel.selectedRecord
            }
        } else if (viewModel.fieldPendingToSetAfterNavigateBack == AddTransactionViewModel.Companion.FIELDS.TO_ACCOUNT) {
            if (selectAccountViewModel.selectedRecord != null) {
                viewModel.toAccount.value = selectAccountViewModel.selectedRecord
            }
        } else if (viewModel.fieldPendingToSetAfterNavigateBack == AddTransactionViewModel.Companion.FIELDS.CATEGORY) {
            if (selectCategoryViewModel.selectedRecord != null) {
                viewModel.category.value = selectCategoryViewModel.selectedRecord
            }
        }
        if(viewModel.selectedAccount.value == null && args.sourceAccountId != null) {
            dbInstance.accountDao()
                .getAccountById(args.sourceAccountId!!)
                .observe(viewLifecycleOwner, Observer { account -> viewModel.selectedAccount.value = account; viewModel.selectTransactionType(viewModel.transactionType.value!!) })
        }
        viewModel.navigateBack.observe(viewLifecycleOwner, Observer { value ->
            if (value) {
                findNavController().popBackStack()
                viewModel.navigateBack.value = false
            }
        })
        clearViewModelFlags()
        return view
    }

    private fun clearViewModelFlags() {
        viewModel.fieldPendingToSetAfterNavigateBack = AddTransactionViewModel.Companion.FIELDS.NONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        print("view created")
        val fromAccountView = view.findViewById<View>(R.id.fromAccountInput)
        fromAccountView.setOnClickListener {
            viewModel.fieldPendingToSetAfterNavigateBack =
                AddTransactionViewModel.Companion.FIELDS.FROM_ACCOUNT
            val action = AddTransactionDirections.actionAddTransactionToSelectAccount()
            selectAccountViewModel.mode = CommonSelectViewModel.Companion.SELECTION_MODE.SINGLE
            view.findNavController().navigate(action)
        }
        val fromAccountLayout = view.findViewById<TextInputLayout>(R.id.fromAccount)
        fromAccountLayout.setEndIconOnClickListener {
            if(viewModel.fromAccount.value != null) {
                viewModel.amount.value = viewModel.fromAccount.value!!.balance.toString()
            }
        }
        val toAccountView = view.findViewById<View>(R.id.toAccountInput)
        toAccountView.setOnClickListener {
            viewModel.fieldPendingToSetAfterNavigateBack =
                AddTransactionViewModel.Companion.FIELDS.TO_ACCOUNT
            val action = AddTransactionDirections.actionAddTransactionToSelectAccount()
            selectAccountViewModel.mode = CommonSelectViewModel.Companion.SELECTION_MODE.SINGLE
            view.findNavController().navigate(action)
        }
        val toAccountLayout = view.findViewById<TextInputLayout>(R.id.toAccount)
        toAccountLayout.setEndIconOnClickListener {
            if(viewModel.toAccount.value != null) {
                viewModel.amount.value = viewModel.toAccount.value!!.balance.toString()
            }
        }
        val categoryView = view.findViewById<View>(R.id.category_input)
        categoryView.setOnClickListener {
            viewModel.fieldPendingToSetAfterNavigateBack =
                AddTransactionViewModel.Companion.FIELDS.CATEGORY
            val action = AddTransactionDirections.actionAddTransactionToTransactionCategory((viewModel.transactionType.value?:0).toString())
            selectCategoryViewModel.mode = CommonSelectViewModel.Companion.SELECTION_MODE.SINGLE
            action.selectCategory = true
            view.findNavController().navigate(action)
        }
    }

}