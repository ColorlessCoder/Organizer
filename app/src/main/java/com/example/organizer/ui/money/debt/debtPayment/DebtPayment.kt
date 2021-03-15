package com.example.organizer.ui.money.debt.debtPayment

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBinderMapper
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.entity.Transaction
import com.example.organizer.database.enums.DebtType
import com.example.organizer.database.enums.TransactionType
import com.example.organizer.databinding.DebtPaymentFragmentBinding
import com.example.organizer.ui.money.common.CommonSelectViewModel
import com.example.organizer.ui.money.selectAccount.SelectAccountViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*

class DebtPayment : Fragment() {

    companion object {
        fun newInstance() = DebtPayment()
    }

    private lateinit var viewModel: DebtPaymentViewModel
    private lateinit var selectAccountViewModel: SelectAccountViewModel
    val args: DebtPaymentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<DebtPaymentFragmentBinding>(
            inflater,
            R.layout.debt_payment_fragment,
            container,
            false
        )
        viewModel = ViewModelProvider(this).get(DebtPaymentViewModel::class.java)
        selectAccountViewModel =
            ViewModelProvider(requireActivity()).get(SelectAccountViewModel::class.java)
        binding.viewModel = viewModel
        val db = AppDatabase.getInstance(requireContext())
        if (viewModel.navigatedToSet == DebtPaymentViewModel.Companion.NavigatedToSet.ACCOUNT) {
            viewModel.account.value = selectAccountViewModel.selectedRecord
        } else {
            db.debtDao().getDebtById(args.debtId).observe(this, Observer {
                viewModel.debt = it
            })
        }
        viewModel.navigatedToSet = DebtPaymentViewModel.Companion.NavigatedToSet.NONE
        val view = binding.root
        view.findViewById<TextInputEditText>(R.id.accountInput)
            .setOnClickListener {
                viewModel.navigatedToSet = DebtPaymentViewModel.Companion.NavigatedToSet.ACCOUNT
                val action = DebtPaymentDirections.debtPaymentToSelectAccount()
                selectAccountViewModel.mode = CommonSelectViewModel.Companion.SELECTION_MODE.SINGLE
                selectAccountViewModel.selectedRecord = viewModel.account.value
                findNavController().navigate(action)
            }
        view.findViewById<Button>(R.id.make_payment).setOnClickListener {
            if (viewModel.amount.value.isNullOrEmpty() || viewModel.amount.value!!.toDouble()
                    .compareTo(0.0) <= 0
            ) {
                Toast.makeText(
                    requireContext(),
                    "Amount cannot be blank or zero",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (viewModel.account.value == null) {
                Toast.makeText(
                    requireContext(),
                    "Please select an account",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                lifecycleScope.launch {
                    val transactionType = DebtType.from(viewModel.debt.debtType).relatedPaymentTransactionType
                    try {
                        db.transactionDao().insertAndUpdateAccount(
                            Transaction(
                                UUID.randomUUID().toString(),
                                transactionType.typeCode,
                                viewModel.amount.value!!.toDouble(),
                                if(transactionType == TransactionType.EXPENSE) viewModel.account.value!!.id else null,
                                if(transactionType == TransactionType.INCOME) viewModel.account.value!!.id else null,
                                null,
                                null,
                                viewModel.details.value,
                                Date().time,
                                viewModel.debt.id
                            )
                        )
                        findNavController().popBackStack()
                    } catch (ex:Exception) {
                        Toast.makeText(requireContext(),ex.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return binding.root
    }

}