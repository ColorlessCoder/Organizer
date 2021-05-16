package com.example.organizer.ui.money.debt.editDebt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.organizer.MainActivity
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.enums.DebtType
import com.example.organizer.databinding.EditDebtFragmentBinding
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.ui.money.common.CommonSelectViewModel
import com.example.organizer.ui.money.selectAccount.SelectAccountViewModel
import com.example.organizer.ui.money.debt.selectDebtType.SelectDebtTypeViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import java.util.*

class EditDebt : Fragment() {

    companion object {
        fun newInstance() = EditDebt()
    }

    private lateinit var viewModel: EditDebtViewModel
    private lateinit var selectAccountViewModel: SelectAccountViewModel
    private lateinit var selectDebtTypeViewModel: SelectDebtTypeViewModel
    val args: EditDebtArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val actionBarActivity: MainActivity = activity as MainActivity
        actionBarActivity.supportActionBar?.title =
            if (args.debtId == null) "Create Debt" else "Edit Debt"
        val binding = DataBindingUtil.inflate<EditDebtFragmentBinding>(
            inflater,
            R.layout.edit_debt_fragment,
            container,
            false
        );
        val view = binding.root
        viewModel = ViewModelProviders.of(this).get(EditDebtViewModel::class.java)
        val dbInstance = AppDatabase.getInstance(requireContext())
        viewModel.transactionDAO = dbInstance.transactionDao()
        selectAccountViewModel =
            ViewModelProviders.of(requireActivity()).get(SelectAccountViewModel::class.java)
        selectDebtTypeViewModel =
            ViewModelProviders.of(requireActivity()).get(SelectDebtTypeViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        if (viewModel.fieldPendingToSetAfterNavigateBack == EditDebtViewModel.Companion.FIELDS.ACCOUNT
            && selectAccountViewModel.selectedRecord != null
        ) {
            viewModel.account.value = selectAccountViewModel.selectedRecord
            viewModel.accountName.value = selectAccountViewModel.selectedRecord?.accountName
        } else if (viewModel.fieldPendingToSetAfterNavigateBack == EditDebtViewModel.Companion.FIELDS.DEBT_TYPE
            && selectDebtTypeViewModel.selectedRecord != null
        ) {
            viewModel.selectDebtType(selectDebtTypeViewModel.selectedRecord ?: DebtType.BORROWED)
        } else {
            if (args.debtId != null) {
                dbInstance.debtDao()
                    .getDebtById(args.debtId!!)
                    .observe(
                        this,
                        Observer {
                            viewModel.setDebtRecord(it)
                        })
            }
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

    private fun clearViewModelFlags() {
        viewModel.fieldPendingToSetAfterNavigateBack = EditDebtViewModel.Companion.FIELDS.NONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        print("view created")
        if (args.debtId == null) {
            val debtTypeView = view.findViewById<View>(R.id.debt_type_input)
            debtTypeView.setOnClickListener {
                viewModel.fieldPendingToSetAfterNavigateBack =
                    EditDebtViewModel.Companion.FIELDS.DEBT_TYPE
                selectDebtTypeViewModel.mode = CommonSelectViewModel.Companion.SELECTION_MODE.SINGLE
                selectDebtTypeViewModel.selectedRecord = viewModel.debtType.value
                findNavController().navigate(EditDebtDirections.actionEditDebtToSelectDebtType())
            }
            val fromAccountView = view.findViewById<View>(R.id.account_input)
            fromAccountView.setOnClickListener {
                viewModel.fieldPendingToSetAfterNavigateBack =
                    EditDebtViewModel.Companion.FIELDS.ACCOUNT
                val action = EditDebtDirections.actionEditDebtToSelectAccount()
                selectAccountViewModel.mode = CommonSelectViewModel.Companion.SELECTION_MODE.SINGLE
                selectAccountViewModel.selectedRecord = viewModel.account.value
                view.findNavController().navigate(action)
            }
        } else {
            view.findViewById<View>(R.id.account).visibility = View.GONE
            view.findViewById<TextInputLayout>(R.id.amount).isEnabled = false
            view.findViewById<TextInputLayout>(R.id.paid_so_far).isEnabled = false
        }
        val dueDateField = view.findViewById<TextInputLayout>(R.id.due_date)
        dueDateField.setStartIconOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Due Date")
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(viewModel.dueDate?.time?.time)
                .build()
            datePicker.addOnPositiveButtonClickListener {
                if (it != null) {
                    val cal = Calendar.getInstance()
                    cal.time = Date(it)
                    if (viewModel.dueDate == null) {
                        cal.set(Calendar.HOUR, 0)
                        cal.set(Calendar.MINUTE, 0)
                    } else {
                        cal.set(Calendar.HOUR, viewModel.dueDate!!.get(Calendar.HOUR))
                        cal.set(Calendar.MINUTE, viewModel.dueDate!!.get(Calendar.MINUTE))
                    }
                    viewModel.dueDate = cal
                    viewModel.dueDateText.value = DateUtils.getDateString(cal.time)
                }
            }
            activity?.supportFragmentManager?.let { it1 ->
                datePicker.show(it1, "editDebtDueDatePicker")
            }
        }
        val dueTimeField = view.findViewById<TextInputLayout>(R.id.due_time)
        dueTimeField.setStartIconOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTitleText("Select Due Date")
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setHour(viewModel.dueDate?.get(Calendar.HOUR) ?: 0)
                .setMinute(viewModel.dueDate?.get(Calendar.MINUTE) ?: 0)
                .build()
            timePicker.addOnPositiveButtonClickListener {
                viewModel.dueDate?.set(Calendar.HOUR, timePicker.hour)
                viewModel.dueDate?.set(Calendar.MINUTE, timePicker.minute)
                viewModel.dueTimeText.value = timePicker.hour.toString() + ":" + timePicker.minute
            }
            activity?.supportFragmentManager?.let { it1 ->
                timePicker.show(it1, "editDebtDueTimePicker")
            }
        }
        view.findViewById<Button>(R.id.save_button).setOnClickListener {
            val message = viewModel.validation()
            if (message.isEmpty()) {
                viewModel.saveDebt()
            } else {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}