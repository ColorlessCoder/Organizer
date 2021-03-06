package com.example.organizer.ui.money.viewTransaction

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.MainActivity
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.Enums.TransactionType
import com.example.organizer.database.relation.TransactionDetails
import com.example.organizer.databinding.EditAccountFragmentBinding
import com.example.organizer.databinding.ViewTransactionFragmentBinding
import com.example.organizer.ui.Utils.ColorUtil
import com.example.organizer.ui.Utils.DateUtils
import com.example.organizer.ui.Utils.ShpaeUtil
import com.example.organizer.ui.Utils.StringUtils
import com.example.organizer.ui.money.ColorSpinnerAdapter
import com.example.organizer.ui.money.common.CommonSelectViewModel
import com.example.organizer.ui.money.editAccount.EditAccountViewModel
import com.example.organizer.ui.money.selectAccount.SelectAccountViewModel
import com.example.organizer.ui.money.selectTransactionType.SelectTransactionTypeViewModel
import com.example.organizer.ui.money.transactionCategory.SelectCategoryViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.util.*


class ViewTransaction : Fragment() {

    companion object {
        fun newInstance() = ViewTransaction()
    }

    private lateinit var viewModel: ViewTransactionViewModel
    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var selectAccountViewModel: SelectAccountViewModel
    private lateinit var selectCategoryViewModel: SelectCategoryViewModel
    private lateinit var selectTransactionTypeViewModel: SelectTransactionTypeViewModel
    private val args: ViewTransactionArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val actionBarActivity: MainActivity = activity as MainActivity
        actionBarActivity.supportActionBar?.title = "Transactions"
        val binding = DataBindingUtil.inflate<ViewTransactionFragmentBinding>(
            inflater,
            R.layout.view_transaction_fragment,
            container,
            false
        );
        val coordinatorLayout = binding.root  as CoordinatorLayout
        viewModel = ViewModelProvider(this).get(ViewTransactionViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        selectAccountViewModel =
            ViewModelProvider(requireActivity()).get(SelectAccountViewModel::class.java)
        selectCategoryViewModel =
            ViewModelProvider(requireActivity()).get(SelectCategoryViewModel::class.java)
        selectTransactionTypeViewModel =
            ViewModelProvider(requireActivity()).get(SelectTransactionTypeViewModel::class.java)
        loadUiAsPerViewModel(coordinatorLayout)
        setBottomSheet(coordinatorLayout)
        return coordinatorLayout
    }

    private fun loadUiAsPerViewModel(parentView: View) {
        if (viewModel.fieldPendingToSetAfterNavigateBack == ViewTransactionViewModel.Companion.FIELDS.NONE) {
            viewModel.clear()
        } else if (viewModel.fieldPendingToSetAfterNavigateBack == ViewTransactionViewModel.Companion.FIELDS.ACCOUNT) {
            viewModel.filterAccountValue =
                selectAccountViewModel.selectedRecords.map { it }.toMutableList()
            when {
                selectAccountViewModel.allSelected -> viewModel.filterAccountText.value = viewModel.ALL
                selectAccountViewModel.selectedRecords.isEmpty() -> viewModel.filterAccountText.value =
                    viewModel.EMPTY
                else -> viewModel.filterAccountText.value =
                    selectAccountViewModel.selectedRecords.joinToString(separator = ", ") { it.accountName }
            }
        } else if (viewModel.fieldPendingToSetAfterNavigateBack == ViewTransactionViewModel.Companion.FIELDS.CATEGORY) {
            viewModel.filterCategoryValue =
                selectCategoryViewModel.selectedRecords.map { it }.toMutableList()
            when {
                selectCategoryViewModel.allSelected -> viewModel.filterCategoryText.value = viewModel.ALL
                selectCategoryViewModel.selectedRecords.isEmpty() -> viewModel.filterCategoryText.value =
                    viewModel.EMPTY
                else -> viewModel.filterCategoryText.value =
                    selectCategoryViewModel.selectedRecords.joinToString(separator = ", ") { it.categoryName }
            }
        } else if (viewModel.fieldPendingToSetAfterNavigateBack == ViewTransactionViewModel.Companion.FIELDS.TYPE) {
            viewModel.filterTypeValue =
                selectTransactionTypeViewModel.selectedRecords.map { it }.toMutableList()
            when {
                selectTransactionTypeViewModel.allSelected -> viewModel.filterTypeText.value =
                    viewModel.ALL
                selectTransactionTypeViewModel.selectedRecords.isEmpty() -> viewModel.filterTypeText.value =
                    viewModel.EMPTY
                else -> viewModel.filterTypeText.value =
                    selectTransactionTypeViewModel.selectedRecords.joinToString(separator = ", ") { it.name }
            }
        }
        loadFilter(parentView)
        viewModel.fieldPendingToSetAfterNavigateBack =
            ViewTransactionViewModel.Companion.FIELDS.NONE
    }

    private fun loadFilter(parentView: View) {
        loadDaysFilter(parentView)
        loadTypeFilter(parentView)
        loadAccountFilter(parentView)
        loadCategoryFilter(parentView)
    }

    private fun loadDaysFilter(parentView: View) {
        val filterDaysInput = parentView.findViewById<TextInputEditText>(R.id.filter_days_input)
        filterDaysInput.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (viewModel.previousFilterDays != viewModel.filterDays.value) {
                    viewModel.previousFilterDays = viewModel.filterDays.value?: "30"
                    loadTransactions(parentView)
                }
            }
        }
    }

    private fun loadTypeFilter(parentView: View) {
        val filterTransactionTypeInput =   parentView.findViewById<TextInputEditText>(R.id.filter_transaction_type_input)
        filterTransactionTypeInput.setOnClickListener {
            selectTransactionTypeViewModel.allSelected = viewModel.filterTypeText.value == viewModel.ALL
            selectTransactionTypeViewModel.selectedRecords = viewModel.filterTypeValue
            selectTransactionTypeViewModel.mode =
                CommonSelectViewModel.Companion.SELECTION_MODE.MULTIPLE
            viewModel.fieldPendingToSetAfterNavigateBack =
                ViewTransactionViewModel.Companion.FIELDS.TYPE
            val action = ViewTransactionDirections.actionViewTransactionToSelectTransactionType()
            findNavController().navigate(action);
        }
    }

    private fun loadAccountFilter(parentView: View) {
        val filterAccountInput =
            parentView.findViewById<TextInputEditText>(R.id.filter_account_input)
        filterAccountInput.setOnClickListener {
            selectAccountViewModel.allSelected = viewModel.filterAccountText.value == viewModel.ALL
            selectAccountViewModel.selectedRecords = viewModel.filterAccountValue
            selectAccountViewModel.mode = CommonSelectViewModel.Companion.SELECTION_MODE.MULTIPLE
            viewModel.fieldPendingToSetAfterNavigateBack =
                ViewTransactionViewModel.Companion.FIELDS.ACCOUNT
            val action = ViewTransactionDirections.actionViewTransactionToSelectAccount()
            findNavController().navigate(action);
        }
    }

    private fun loadCategoryFilter(parentView: View) {
        val filterCategoryInput =
            parentView.findViewById<TextInputEditText>(R.id.filter_category_input)
        filterCategoryInput.setOnClickListener {
            selectCategoryViewModel.allSelected = viewModel.filterCategoryText.value == viewModel.ALL
            selectCategoryViewModel.selectedRecords = viewModel.filterCategoryValue
            selectCategoryViewModel.mode = CommonSelectViewModel.Companion.SELECTION_MODE.MULTIPLE
            viewModel.fieldPendingToSetAfterNavigateBack =
                ViewTransactionViewModel.Companion.FIELDS.CATEGORY
            val action = ViewTransactionDirections.actionViewTransactionToTransactionCategory("<ALL>")
            action.selectCategory = true
            findNavController().navigate(action);
        }
    }

    private fun setBottomSheet(coordinatorLayout: CoordinatorLayout) {

        val filterIcon = coordinatorLayout.findViewById<View>(R.id.filterIcon)
        val contentLayout: LinearLayout = coordinatorLayout.findViewById(R.id.contentLayout)

        sheetBehavior = BottomSheetBehavior.from(contentLayout)
        sheetBehavior.isFitToContents = false
        sheetBehavior.isHideable =
            false //prevents the bottom sheet from completely hiding off the screen

        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED //initially state to fully expanded

        filterIcon.setOnClickListener { toggleFilters() }
    }

    private fun toggleFilters() {
        if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTransactions(view)
    }

    fun loadTransactions(view: View) {
        val transactionList: RecyclerView = view.findViewById(R.id.view_transaction_list);
        val transactionDAO = AppDatabase.getInstance(view.context).transactionDao()
        lifecycleScope.launch {
            val transactionDetailsList = transactionDAO.getTransactionDetailsAsPerRawQuery(transactionDAO.getAllTransactionDetailsQueryForFilter(
                if (viewModel.filterAccountText.value == viewModel.ALL) null else viewModel.filterAccountValue.map { it.id },
                if (viewModel.filterCategoryText.value == viewModel.ALL) null else viewModel.filterCategoryValue.map { it.id },
                if (viewModel.filterTypeText.value == viewModel.ALL) null else viewModel.filterTypeValue.map { it.typeCode },
                (viewModel.filterDays.value?:"30").toInt()
            ))
            updateTotalFields(view, transactionDetailsList)
            transactionList.adapter =
                ViewTransactionListAdapter(
                    transactionDetailsList,
                    view
                )
        }
    }

    private fun updateTotalFields(view:View, transactionDetailsList: List<TransactionDetails>) {
        view.findViewById<TextView>(R.id.total_income).text = transactionDetailsList
            .filter { it.transaction.transactionType == TransactionType.INCOME.typeCode }
            .fold(0.0){acc: Double, transactionDetails: TransactionDetails -> transactionDetails.transaction.amount + acc}
            .toString() + "BDT"
        view.findViewById<TextView>(R.id.total_expense).text = transactionDetailsList
            .filter { it.transaction.transactionType == TransactionType.EXPENSE.typeCode }
            .fold(0.0){acc: Double, transactionDetails: TransactionDetails -> transactionDetails.transaction.amount + acc}
            .toString() + "BDT"
    }

    class ViewTransactionListAdapter(
        private val transactionDetailsList: List<TransactionDetails>,
        val view: View
    ) : RecyclerView.Adapter<ViewTransactionListAdapter.ViewHolder>() {
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val transactionType: TextView = view.findViewById(R.id.transaction_type)
            val transactionComment: TextView = view.findViewById(R.id.transaction_comment)
            val transactionAmount: TextView = view.findViewById(R.id.transacted_amount)
            val fromAccount: TextView = view.findViewById(R.id.from_account)
            val toAccount: TextView = view.findViewById(R.id.to_account)
            val transactionDate: TextView = view.findViewById(R.id.transaction_date)
            val categoryName: TextView = view.findViewById(R.id.category_name)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.view_transaction_row, parent, false)
            return ViewHolder(view)
        }

        override fun getItemCount(): Int {
            return transactionDetailsList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val transactionDetails = transactionDetailsList.get(position)
            val transactionType =
                TransactionType.from(transactionDetails.transaction.transactionType)
            holder.transactionType.text = transactionType.name
            holder.transactionType.background = ShpaeUtil.getRoundCornerShape(
                15.toFloat(),
                ContextCompat.getColor(view.context, transactionType.color),
                null
            )
            holder.transactionComment.text = transactionDetails.transaction.details
            holder.transactionAmount.text =
                StringUtils.doubleToString(transactionDetails.transaction.amount)
            holder.fromAccount.text = transactionDetails.fromAccountName
            holder.toAccount.text = transactionDetails.toAccountName
            if (transactionDetails.categoryName == null) {
                holder.categoryName.visibility = View.GONE
            } else {
                holder.categoryName.text = transactionDetails.categoryName
                holder.categoryName.background = ShpaeUtil.getRoundCornerShape(
                    15.toFloat(),
                    Color.WHITE,
                    ContextCompat.getColor(view.context, transactionType.color)
                )
            }
            holder.transactionDate.text =
                DateUtils.dateToString(Date(transactionDetails.transaction.transactedAt));
        }

    }

}