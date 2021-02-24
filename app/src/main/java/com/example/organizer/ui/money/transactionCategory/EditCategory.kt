package com.example.organizer.ui.money.transactionCategory

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.Enums.TransactionType
import com.example.organizer.databinding.EditAccountFragmentBinding
import com.example.organizer.databinding.EditCategoryFragmentBinding
import com.example.organizer.ui.Utils.ColorUtil
import com.example.organizer.ui.money.ColorSpinnerAdapter
import com.example.organizer.ui.money.editAccount.EditAccountArgs
import com.example.organizer.ui.money.editAccount.EditAccountViewModel

class EditCategory : Fragment() {

    companion object {
        fun newInstance() = EditCategory()
    }

    private lateinit var viewModel: EditCategoryViewModel
    val args: EditCategoryArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<EditCategoryFragmentBinding>(
            inflater,
            R.layout.edit_category_fragment,
            container,
            false
        )
        val view = binding.root
        viewModel = ViewModelProviders.of(this).get(EditCategoryViewModel::class.java)
        binding.editCategoryViewModel = viewModel
        val dbInstance = AppDatabase.getInstance(view.context)
        if(args.transactionType != null) {
            viewModel.transactionType.value = TransactionType.from(args.transactionType)
        }
        viewModel.categoryDAO = dbInstance.categoryDao()
        binding.lifecycleOwner = this
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.view = view
    }

}