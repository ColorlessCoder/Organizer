package com.example.organizer.ui.money.transactionChart

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.navArgs
import com.example.organizer.MainActivity
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.enums.ChartXType
import com.example.organizer.databinding.EditAccountFragmentBinding
import com.example.organizer.databinding.EditChartFragmentBinding
import com.example.organizer.ui.money.editAccount.EditAccountArgs
import com.example.organizer.ui.money.editAccount.EditAccountViewModel
import com.google.android.material.textfield.TextInputLayout

class EditChart : Fragment() {

    companion object {
        fun newInstance() = EditChart()
    }

    private lateinit var viewModel: EditChartViewModel

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
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.transactionChartDAO = AppDatabase.getInstance(requireContext()).transactionChartDao()
        viewModel.chartOrder = args.order
        if(args.transactionChartId != null) {
            viewModel.transactionChartDAO.getChartById(args.transactionChartId!!)
                .observe(viewLifecycleOwner, Observer {
                    viewModel.setChart(it)
                })
        }
        val items = listOf(ChartXType.LABEL.name, ChartXType.DATE.name)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        val textField = view.findViewById<TextInputLayout>(R.id.show_count_by)
        (textField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        viewModel.view = view
        return view
    }

}