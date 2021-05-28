package com.example.organizer.ui.money.transactionChart

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.organizer.MainActivity
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.enums.ChartXType
import com.example.organizer.databinding.EditChartFragmentBinding
import com.example.organizer.ui.money.common.CommonSelectViewModel
import com.example.organizer.ui.money.transactionCategory.SelectCategoryViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class EditChart : Fragment() {

    companion object {
        fun newInstance() = EditChart()
    }

    private lateinit var viewModel: EditChartViewModel
    private lateinit var selectCategoryViewModel: SelectCategoryViewModel

    val args: EditChartArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val isCreating = args.transactionChartId == null
        val actionBarActivity: MainActivity = activity as MainActivity
        actionBarActivity.supportActionBar?.title =
            if (isCreating) "Create Chart" else "Edit Chart"
        val binding = DataBindingUtil.inflate<EditChartFragmentBinding>(
            inflater,
            R.layout.edit_chart_fragment,
            container,
            false
        );
        val view = binding.root
        viewModel = ViewModelProvider(this).get(EditChartViewModel::class.java)
        selectCategoryViewModel =
            ViewModelProvider(requireActivity()).get(SelectCategoryViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.transactionChartDAO = AppDatabase.getInstance(requireContext()).transactionChartDao()
        viewModel.chartOrder = args.order
        if(viewModel.fieldPendingToSetAfterNavigateBack == EditChartViewModel.Companion.FIELDS.CATEGORY) {
            viewModel.filterCategoryIds = selectCategoryViewModel.selectedRecords.map { it.id }.toMutableList()
        } else if(args.transactionChartId != null) {
            lifecycleScope.launch {
                viewModel.setChart(viewModel.transactionChartDAO.getChartByIdSuspend(args.transactionChartId!!))
                viewModel.filterCategoryIds = viewModel.transactionChartDAO.getCategoriesRelatedToChart(args.transactionChartId!!).map { it.category_id }.toMutableList()
            }
            viewModel.transactionChartDAO.getChartById(args.transactionChartId!!)
                .observe(viewLifecycleOwner, Observer {
                    viewModel.setChart(it)
                })
        }
        viewModel.fieldPendingToSetAfterNavigateBack = EditChartViewModel.Companion.FIELDS.NONE
        val items = listOf(ChartXType.LABEL.name, ChartXType.DATE.name)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        val textField = view.findViewById<TextInputLayout>(R.id.show_count_by)
        (textField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        viewModel.view = view
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.filter_category_button)
            .setOnClickListener {
                viewModel.fieldPendingToSetAfterNavigateBack = EditChartViewModel.Companion.FIELDS.CATEGORY
                selectCategoryViewModel.mode = CommonSelectViewModel.Companion.SELECTION_MODE.MULTIPLE
                selectCategoryViewModel.argSelectedIds = viewModel.filterCategoryIds
                selectCategoryViewModel.allSelected = false
                val action = EditChartDirections.actionEditChartToTransactionCategory("<ALL>")
                action.selectCategory = true
                action.includeNoCategory = true
                findNavController().navigate(action)
            }
    }

}