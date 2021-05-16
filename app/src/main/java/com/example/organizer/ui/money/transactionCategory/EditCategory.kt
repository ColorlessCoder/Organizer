package com.example.organizer.ui.money.transactionCategory

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
import com.example.organizer.database.enums.TransactionType
import com.example.organizer.databinding.EditCategoryFragmentBinding

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
        val actionBarActivity: MainActivity = activity as MainActivity
        actionBarActivity.supportActionBar?.title =
            if (args.id.isNullOrEmpty()) "Create Category" else "Edit Category"
        val binding = DataBindingUtil.inflate<EditCategoryFragmentBinding>(
            inflater,
            R.layout.edit_category_fragment,
            container,
            false
        )
        val view = binding.root
        viewModel = ViewModelProviders.of(this).get(EditCategoryViewModel::class.java)
        viewModel.isCreating.observe(viewLifecycleOwner, Observer {
            actionBarActivity.supportActionBar?.title =
                if (it == true) "Create Category" else "Edit Category"
        })
        binding.editCategoryViewModel = viewModel
        val dbInstance = AppDatabase.getInstance(view.context)
        if (args.transactionType != null) {
            viewModel.transactionType.value = TransactionType.from(args.transactionType)
        }
        viewModel.categoryDAO = dbInstance.categoryDao()
        binding.lifecycleOwner = this
        if (args.id != null) {
            dbInstance.categoryDao()
                .getCategory(args.id!!)
                .observe(viewLifecycleOwner, Observer {
                    viewModel.category = it
                    viewModel.setFullCategoryName(viewModel.category!!.categoryName)
                    viewModel.showDelete.value = true
                    viewModel.showClone.value = true
                    viewModel.isCreating.value = false
                })
        } else {
            viewModel.category = null
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.view = view
    }

}