package com.example.organizer.ui.money.transactionPlan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.organizer.MainActivity
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.databinding.EditTemplateTransactionFragmentBinding
import com.example.organizer.ui.money.selectAccount.SelectAccountViewModel
import com.example.organizer.ui.money.transactionCategory.SelectCategoryViewModel
import kotlinx.coroutines.launch

class EditTemplateTransaction : Fragment() {

    companion object {
        fun newInstance() = EditTemplateTransaction()
    }

    private lateinit var viewModel: EditTemplateTransactionViewModel
    private lateinit var selectAccountViewModel: SelectAccountViewModel
    private lateinit var selectCategoryViewModel: SelectCategoryViewModel
    private val args: EditTemplateTransactionArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val actionBarActivity: MainActivity = activity as MainActivity
        actionBarActivity.supportActionBar?.title = if(args.templateTransactionId == null)"Create Template" else "Edit Template"
        val binding = DataBindingUtil.inflate<EditTemplateTransactionFragmentBinding>(
            inflater,
            R.layout.edit_template_transaction_fragment,
            container,
            false
        );
        val view = binding.root
        viewModel = ViewModelProviders.of(this).get(EditTemplateTransactionViewModel::class.java)
        val dbInstance = AppDatabase.getInstance(requireContext())
        viewModel.templateTransactionDAO = dbInstance.templateTransactionDao()
        selectAccountViewModel =
            ViewModelProviders.of(requireActivity()).get(SelectAccountViewModel::class.java)
        selectCategoryViewModel =
            ViewModelProviders.of(requireActivity()).get(SelectCategoryViewModel::class.java)
        binding.editTemplateTransactionModel = viewModel
        binding.lifecycleOwner = this
        if (viewModel.fieldPendingToSetAfterNavigateBack == EditTemplateTransactionViewModel.Companion.FIELDS.FROM_ACCOUNT) {
            if (selectAccountViewModel.selectedRecord != null) {
                viewModel.fromAccount.value = selectAccountViewModel.selectedRecord
            }
        } else if (viewModel.fieldPendingToSetAfterNavigateBack == EditTemplateTransactionViewModel.Companion.FIELDS.TO_ACCOUNT) {
            if (selectAccountViewModel.selectedRecord != null) {
                viewModel.toAccount.value = selectAccountViewModel.selectedRecord
            }
        } else if (viewModel.fieldPendingToSetAfterNavigateBack == EditTemplateTransactionViewModel.Companion.FIELDS.CATEGORY) {
            if (selectCategoryViewModel.selectedRecord != null) {
                viewModel.category.value = selectCategoryViewModel.selectedRecord
            }
        } else {
            updateViewModelAsPerArgs(dbInstance)
        }
        viewModel.navigateBack.observe(this, Observer { value ->
            if (value) {
                findNavController().popBackStack()
                viewModel.navigateBack.value = false
            }
        })
        clearViewModelFlags()
        return view
    }

    private fun updateViewModelAsPerArgs(dbInstance: AppDatabase) {
        viewModel.transactionPlanId = args.transactionPlanId
        viewModel.order = args.order
        if (args.templateTransactionId != null) {
            dbInstance
                .templateTransactionDao()
                .getById(args.templateTransactionId!!)
                .observe(this, Observer {
                    viewModel.templateTransaction = it
                    updateViewModelForCurrentTemplate(dbInstance)
                })
        }
    }

    private fun updateViewModelForCurrentTemplate(dbInstance: AppDatabase) {
        if (viewModel.templateTransaction != null) {
            val templateTransaction = viewModel.templateTransaction!!
            viewModel.selectTransactionType(templateTransaction.transactionType)
            viewModel.amount.value = templateTransaction.amount.toString()
            viewModel.details.value = templateTransaction.details
            lifecycleScope.launch {
                if (templateTransaction.fromAccount != null) {
                    viewModel.fromAccount.value =
                        dbInstance.accountDao().getById(templateTransaction.fromAccount!!)
                }
                if (templateTransaction.toAccount != null) {
                    viewModel.toAccount.value =
                        dbInstance.accountDao().getById(templateTransaction.toAccount!!)
                }
                if (templateTransaction.transactionCategoryId != null) {
                    viewModel.category.value =
                        dbInstance.categoryDao().getById(templateTransaction.transactionCategoryId!!)
                }
            }
        }
    }

    private fun clearViewModelFlags() {
        viewModel.fieldPendingToSetAfterNavigateBack =
            EditTemplateTransactionViewModel.Companion.FIELDS.NONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        print("view created")
        val fromAccountView = view.findViewById<View>(R.id.fromAccountInput)
        fromAccountView.setOnClickListener {
            viewModel.fieldPendingToSetAfterNavigateBack =
                EditTemplateTransactionViewModel.Companion.FIELDS.FROM_ACCOUNT
            val action = EditTemplateTransactionDirections.actionEditTemplateTransactionToSelectAccount()
            view.findNavController().navigate(action)
        }
        val toAccountView = view.findViewById<View>(R.id.toAccountInput)
        toAccountView.setOnClickListener {
            viewModel.fieldPendingToSetAfterNavigateBack =
                EditTemplateTransactionViewModel.Companion.FIELDS.TO_ACCOUNT
            val action = EditTemplateTransactionDirections.actionEditTemplateTransactionToSelectAccount()
            view.findNavController().navigate(action)
        }
        val categoryView = view.findViewById<View>(R.id.category_input)
        categoryView.setOnClickListener {
            viewModel.fieldPendingToSetAfterNavigateBack =
                EditTemplateTransactionViewModel.Companion.FIELDS.CATEGORY
            val action = EditTemplateTransactionDirections.actionEditTemplateTransactionToTransactionCategory()
            action.transactionType = viewModel.transactionType.value!!
            action.selectCategory = true
            view.findNavController().navigate(action)
        }
    }

}