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
import com.example.organizer.database.enums.ScheduleIntervalType
import com.example.organizer.database.enums.WeekDays
import com.example.organizer.databinding.EditChartFragmentBinding
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.ui.money.common.CommonSelectViewModel
import com.example.organizer.ui.money.transactionCategory.SelectCategoryViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import kotlinx.coroutines.launch
import java.util.*

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
        viewModel.view = view
        viewModel.clone = args.clone
        viewModel.insert = args.transactionChartId == null || args.clone
        selectCategoryViewModel =
            ViewModelProvider(requireActivity()).get(SelectCategoryViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.transactionChartDAO = AppDatabase.getInstance(requireContext()).transactionChartDao()
        viewModel.chartOrder = args.order
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, viewModel.xTypeList)
        val textField = view.findViewById<TextInputLayout>(R.id.show_count_by)
        (textField.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        (textField.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->  viewModel.setXTypeInView(viewModel.xTypeList[position])}
        viewModel.setXTypeInView(viewModel.xTypeList[0])
        loadGeneratedView(view)
        if(viewModel.fieldPendingToSetAfterNavigateBack == EditChartViewModel.Companion.FIELDS.CATEGORY) {
            viewModel.filterCategoryIds = selectCategoryViewModel.selectedRecords.map { it.id }.toMutableList()
        } else if(args.transactionChartId != null) {
            lifecycleScope.launch {
                viewModel.setChart(viewModel.transactionChartDAO.getChartByIdSuspend(args.transactionChartId!!))
                viewModel.filterCategoryIds = viewModel.transactionChartDAO.getCategoriesRelatedToChart(args.transactionChartId!!).map { it.category_id }.toMutableList()
            }
        }
        viewModel.fieldPendingToSetAfterNavigateBack = EditChartViewModel.Companion.FIELDS.NONE
        return view
    }

    fun loadGeneratedView(view: View) {
        val generateDateField = view.findViewById<TextInputLayout>(R.id.generate_date)
        generateDateField.setStartIconOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Generate on")
                .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
                .setSelection(viewModel.generateAtCalendar?.time?.time)
                .build()
            datePicker.addOnPositiveButtonClickListener {
                if (it != null) {
                    val cal = Calendar.getInstance()
                    cal.time = Date(it)
                    viewModel.setGenerateDate(cal)
                    viewModel.generateAtDate.value = DateUtils.getDateStringWithMonth(cal.time)
                }
            }
            activity?.supportFragmentManager?.let { it1 ->
                datePicker.show(it1, "generateAtDatePicker")
            }
        }
        val generateTimeField = view.findViewById<TextInputLayout>(R.id.generated_time)
        generateTimeField.setStartIconOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTitleText("Generate at")
                .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                .setHour(viewModel.generateAtCalendar?.get(Calendar.HOUR) ?: 0)
                .setMinute(viewModel.generateAtCalendar?.get(Calendar.MINUTE) ?: 0)
                .build()
            timePicker.addOnPositiveButtonClickListener {
                viewModel.setGenerateTime(timePicker.hour, timePicker.minute)
            }
            activity?.supportFragmentManager?.let { it1 ->
                timePicker.show(it1, "generateAtTimePicker")
            }
        }
        val generatedDay = view.findViewById<TextInputLayout>(R.id.generated_day)
        (generatedDay.editText as? AutoCompleteTextView)?.setAdapter(ArrayAdapter(requireContext(), R.layout.list_item, viewModel.weekDays))
        (generatedDay.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->  viewModel.setDayInView(viewModel.weekDays[position])}
        viewModel.setDayInView(viewModel.weekDays[0])
        val autoGenerate = view.findViewById<TextInputLayout>(R.id.auto_generate)
        (autoGenerate.editText as? AutoCompleteTextView)?.setAdapter(ArrayAdapter(requireContext(), R.layout.list_item, viewModel.scheduleTypeList))
        (autoGenerate.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, _, position, _ ->  viewModel.setScheduleTypeInView(viewModel.scheduleTypeList[position])}
        viewModel.setScheduleTypeInView(viewModel.scheduleTypeList[0])
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