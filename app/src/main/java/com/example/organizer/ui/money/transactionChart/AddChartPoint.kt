package com.example.organizer.ui.money.transactionChart

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.organizer.MainActivity
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.databinding.AddChartPointFragmentBinding
import com.example.organizer.databinding.EditChartFragmentBinding

class AddChartPoint : Fragment() {

    companion object {
        fun newInstance() = AddChartPoint()
    }

    private lateinit var viewModel: AddChartPointViewModel

    val args: AddChartPointArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val actionBarActivity: MainActivity = activity as MainActivity
        actionBarActivity.supportActionBar?.title = "Add Point"
        val binding = DataBindingUtil.inflate<AddChartPointFragmentBinding>(
            inflater,
            R.layout.add_chart_point_fragment,
            container,
            false
        );
        val view = binding.root
        viewModel = ViewModelProvider(this).get(AddChartPointViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.transactionChartDAO =
            AppDatabase.getInstance(requireContext()).transactionChartDao()
        viewModel.transactionChartDAO.getChartById(args.transactionChartId)
            .observe(viewLifecycleOwner, Observer {
                viewModel.chart = it
                actionBarActivity.supportActionBar?.title = "Add Point: ${it.chartName}"
                viewModel.updateChartRelatedFields()
            })
        viewModel.view = view
        return view
    }

}