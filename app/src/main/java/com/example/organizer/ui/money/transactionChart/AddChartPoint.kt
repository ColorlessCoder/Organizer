package com.example.organizer.ui.money.transactionChart

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import com.example.organizer.MainActivity
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.databinding.AddChartPointFragmentBinding
import com.example.organizer.databinding.EditChartFragmentBinding
import kotlinx.coroutines.launch

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
        actionBarActivity.supportActionBar?.title = if(args.pointId == 0L) "Add Point" else "Edit Point"
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
        viewModel.viewModelScope.launch {
            viewModel.chart = viewModel.transactionChartDAO.getChartByIdSuspend(args.transactionChartId)
            actionBarActivity.supportActionBar?.title = (if(args.pointId == 0L) "Add Point to " else "Edit Point of ")+
                    viewModel.chart.chartName
            viewModel.updateChartRelatedFields()
            if(args.pointId != 0L) {
                viewModel.setPoint(viewModel.transactionChartDAO.getPointByIdSuspend(args.pointId))
                viewModel.insert.value = false
            }
        }
        viewModel.deleteEnabled.value = args.deletedEnabled
        viewModel.view = view
        return view
    }

}