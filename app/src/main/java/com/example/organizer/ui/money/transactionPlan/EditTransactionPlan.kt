package com.example.organizer.ui.money.transactionPlan

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.example.organizer.MainActivity
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.databinding.EditAccountFragmentBinding
import com.example.organizer.databinding.EditTransactionPlanFragmentBinding
import com.example.organizer.ui.money.ColorSpinnerAdapter
import com.example.organizer.ui.money.editAccount.EditAccountArgs
import com.example.organizer.ui.money.editAccount.EditAccountViewModel

class EditTransactionPlan : Fragment() {

    companion object {
        fun newInstance() = EditTransactionPlan()
    }

    private lateinit var viewModel: EditTransactionPlanViewModel
    private val args: EditTransactionPlanArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val actionBarActivity: MainActivity = activity as MainActivity
        actionBarActivity.supportActionBar?.title =
            if (args.id == null) "Create Transaction Plan" else "Edit Transaction Plan"
        val binding = DataBindingUtil.inflate<EditTransactionPlanFragmentBinding>(
            inflater,
            R.layout.edit_transaction_plan_fragment,
            container,
            false
        );
        val view = binding.root
        viewModel = ViewModelProviders.of(this).get(EditTransactionPlanViewModel::class.java)
        binding.editTransactionPlanViewModel = viewModel
        viewModel.adapter = ColorSpinnerAdapter(viewModel.colors, activity)
        val dbInstance = AppDatabase.getInstance(view.context)
        viewModel.transactionPlanDAO = dbInstance.transactionPlanDao()
        if(args.id != null) {
            dbInstance.transactionPlanDao().getTransactionPlanById(args.id!!)
                .observe(this, Observer {
                    viewModel.transactionPlan = it
                    viewModel.name.value = it.name
                    viewModel.colorIndex.value = viewModel.colors.indexOf(it.color)
                })
        }
        binding.lifecycleOwner = this
        viewModel.view = view
        return view
    }

}