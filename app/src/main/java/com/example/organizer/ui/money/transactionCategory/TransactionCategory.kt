package com.example.organizer.ui.money.transactionCategory

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.enums.TransactionType
import com.example.organizer.database.entity.Category
import com.example.organizer.ui.Utils.ShpaeUtil
import com.example.organizer.ui.money.common.CommonSelectFragment
import com.example.organizer.ui.money.common.CommonSelectRecyclerListAdapter
import com.example.organizer.ui.money.common.CommonSelectViewHolder
import com.example.organizer.ui.money.common.CommonSelectViewModel
import com.example.organizer.ui.money.selectTransactionType.SelectTransactionTypeViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

class TransactionCategory :
    CommonSelectFragment<Category, SelectCategoryViewModel, CategoryListAdapter.ViewHolder, CategoryListAdapter>() {

    companion object {
        fun newInstance() = TransactionCategory()
    }

    private val args: TransactionCategoryArgs by navArgs()
    private lateinit var viewModel: TransactionCategoryViewModel
    private lateinit var currentView: View
    private lateinit var selectTransactionTypeViewModel: SelectTransactionTypeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.transaction_category_fragment, container, false)
    }

    private fun updateCategoryList(categories: List<Category>, view: View) {
        var filteredCategories = getFilteredCategories(categories)
        filteredCategories = sortCategories(filteredCategories)
        setSelectGridAdapter(
            filteredCategories,
            CategoryListAdapter(
                filteredCategories,
                selectViewModel,
                view,
                args.selectCategory
            ),
            filteredCategories.size != categories.size
        )
    }

    private fun sortCategories(categories: List<Category>): List<Category> {
        return categories.sortedWith(compareBy(Category::transactionType, Category::categoryName))
    }

    private fun isRecordMatchedWithFilter(record: Category): Boolean {
        val filterString = viewModel.filterString.value
        val filterGroupString = viewModel.filterGroupString.value
        val recordGroup = EditCategoryViewModel.findCategoryGroup(record.categoryName)
        val recordCategoryName = EditCategoryViewModel.findCategoryName(record.categoryName)
        if(!filterString.isNullOrEmpty() && !recordCategoryName.toLowerCase().startsWith(filterString.toLowerCase())) {
            return false
        }
        if(!filterGroupString.isNullOrEmpty() && !recordGroup.toLowerCase().startsWith(filterGroupString.toLowerCase())) {
            return false
        }
        return true
    }

    private fun getFilteredCategories(categories: List<Category>): List<Category> {
        return categories.filter { r -> isRecordMatchedWithFilter(r) };
    }

    private fun getTransactionTypesInInt(): List<Int>? {
        if(viewModel.transactionTypes.value == null) {
            return null
        }
        return viewModel.transactionTypes.value!!.map { it.typeCode }
    }

    private fun setCategoryListForType(view: View) {
        val categoryDAO = AppDatabase.getInstance(requireContext()).categoryDao()
        lifecycleScope.launch {
            viewModel.allRecords = categoryDAO.getCategories(categoryDAO.getQueryForCategoryTypeIn(getTransactionTypesInInt()))
            updateCategoryList(viewModel.allRecords, view)
        }
    }

    private fun setTransactionTypeFromArg() {
        if (args.transactionTypes == "<ALL>") {
            viewModel.transactionTypes.value = null
        } else if (args.transactionTypes == "<EMPTY>") {
            viewModel.transactionTypes.value = mutableListOf()
        } else {
            viewModel.transactionTypes.value = args.transactionTypes.split(",").map { TransactionType.from(it.toInt()) }
        }
    }

    private fun getFirstType(): TransactionType {
        if(viewModel.transactionTypes.value.isNullOrEmpty()) {
            return TransactionType.TRANSFER
        }
        return viewModel.transactionTypes.value!![0]
    }

    private fun updateTransactionTypeView(button: Button) {
        button.text = getFirstType().name
        button.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                getFirstType().color
            )
        )
    }

    private fun handleSearchCategory(view: View) {
        val searchCategory = view.findViewById<TextInputLayout>(R.id.find_category)
        if(viewModel.filterString.value != null) {
            searchCategory.editText?.setText(viewModel.filterString.value)
        }
        searchCategory.editText?.doOnTextChanged { text, _, _, _ ->  viewModel.filterString.value = text.toString()}
        viewModel.filterString.observe(viewLifecycleOwner, Observer {
            updateCategoryList(viewModel.allRecords, view)
        })
        val searchCategoryGroup = view.findViewById<TextInputLayout>(R.id.find_category_group)
        if(viewModel.filterGroupString.value != null) {
            searchCategoryGroup.editText?.setText(viewModel.filterGroupString.value)
        }
        searchCategoryGroup.editText?.doOnTextChanged { text, _, _, _ ->  viewModel.filterGroupString.value = text.toString()}
        viewModel.filterGroupString.observe(viewLifecycleOwner, Observer {
            updateCategoryList(viewModel.allRecords, view)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentView = view
        viewModel = ViewModelProvider(this).get(TransactionCategoryViewModel::class.java)
        selectViewModel =
            ViewModelProvider(requireActivity()).get(SelectCategoryViewModel::class.java)
        selectViewModel.selectedRecord = null
        selectTransactionTypeViewModel =
            ViewModelProvider(requireActivity()).get(SelectTransactionTypeViewModel::class.java)
        if (viewModel.navigatedToSet == TransactionCategoryViewModel.NAVIGATED_TO_SET.TRANSACTION_TYPE
            && selectTransactionTypeViewModel.selectedRecord != null
        ) {
            viewModel.transactionTypes.value = mutableListOf(selectTransactionTypeViewModel.selectedRecord?:TransactionType.TRANSFER)
        }
        println(selectTransactionTypeViewModel.selectedRecord)
        setCategoryListForType(view)
        handleSearchCategory(view)
        viewModel.transactionTypes.observe(viewLifecycleOwner, Observer {
            setCategoryListForType(view)
        })

        val typeButton = view.findViewById<Button>(R.id.transaction_type_button);
        updateTransactionTypeView(typeButton);

        if (args.selectCategory) {
            setTransactionTypeFromArg()
            view.findViewById<View>(R.id.card_filter).visibility = View.GONE
            view.findViewById<View>(R.id.create_button).visibility = View.GONE
            handleCommonSelectButtons(view);
        } else {
            handleCommonSelectButtons(view, true);
            typeButton.setOnClickListener {
                viewModel.navigatedToSet =
                    TransactionCategoryViewModel.NAVIGATED_TO_SET.TRANSACTION_TYPE
                val action =
                    TransactionCategoryDirections.actionTransactionCategoryToSelectTransactionType()
                selectTransactionTypeViewModel.mode = CommonSelectViewModel.Companion.SELECTION_MODE.SINGLE
                findNavController().navigate(action)
            }
            view.findViewById<View>(R.id.create_button)
                .setOnClickListener {
                    val action =
                        TransactionCategoryDirections.actionTransactionCategoryToEditCategory(null)
                    action.transactionType = getFirstType().typeCode
                    findNavController().navigate(action)
                }
        }
    }

    override fun getSelectRecyclerView(): RecyclerView {
        return currentView.findViewById(R.id.category_list)
    }

}

class CategoryListAdapter(
    private val categories: List<Category>,
    private val viewModel: SelectCategoryViewModel,
    private val parentView: View,
    private val selectCategory: Boolean
) : CommonSelectRecyclerListAdapter<CategoryListAdapter.ViewHolder, Category, SelectCategoryViewModel>(
    categories,
    viewModel,
    parentView
) {
    lateinit var context: Context

    class ViewHolder(view: View) : CommonSelectViewHolder(view) {
        val label: TextView = view.findViewById(R.id.category_label)
        override fun getDrawableElement(): TextView {
            return label
        }
        override fun getTintForSelect(): Int {
            return Color.BLACK
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_list_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories.get(position)
        val type = TransactionType.from(category.transactionType)
        holder.label.text = category.categoryName
        holder.itemView.background = ShpaeUtil.getRoundCornerShape(
            15.toFloat(),
            Color.WHITE,
            ContextCompat.getColor(context, type.color)
        )
        if (!selectCategory) {
            holder.itemView.setOnClickListener {
                val action =
                    TransactionCategoryDirections.actionTransactionCategoryToEditCategory(category.id)
                parentView.findNavController().navigate(action)
            }
        } else {
            super.onBindViewHolder(holder, position)
        }
    }
}