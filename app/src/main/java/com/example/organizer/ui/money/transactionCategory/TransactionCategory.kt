package com.example.organizer.ui.money.transactionCategory

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.AppDatabase
import com.example.organizer.database.Enums.TransactionType
import com.example.organizer.database.entity.Category
import com.example.organizer.ui.Utils.ShpaeUtil
import com.example.organizer.ui.money.common.CommonSelectRecyclerListAdapter
import com.example.organizer.ui.money.common.CommonSelectViewHolder
import com.example.organizer.ui.money.selectTransactionType.SelectTransactionTypeViewModel

class TransactionCategory : Fragment() {

    companion object {
        fun newInstance() = TransactionCategory()
    }

    val args: TransactionCategoryArgs by navArgs()
    private lateinit var viewModel: TransactionCategoryViewModel
    private lateinit var selectCategoryViewModel: SelectCategoryViewModel
    private lateinit var selectTransactionTypeViewModel: SelectTransactionTypeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.transaction_category_fragment, container, false)
    }

    private fun updateCategoryList(categories: List<Category>, view: View) {
        view.findViewById<RecyclerView>(R.id.category_list)
            .adapter = CategoryListAdapter(
            categories,
            selectCategoryViewModel,
            view,
            args.selectCategory
        )
    }

    private fun setCategoryListForType(view: View) {
        val categoryDAO = AppDatabase.getInstance(requireContext()).categoryDao()
        categoryDAO.getCategoriesByType(viewModel.transactionType.value!!.typeCode).observe(this, Observer {
            updateCategoryList(it, view)
        })
    }

    private fun updateTransactionTypeView(button: Button) {
        button.text = viewModel.transactionType.value!!.name
        button.setBackgroundColor(ContextCompat.getColor(requireContext(), viewModel.transactionType.value!!.color))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(TransactionCategoryViewModel::class.java)
        selectCategoryViewModel = ViewModelProvider(requireActivity()).get(SelectCategoryViewModel::class.java)
        selectCategoryViewModel.selectedRecord = null
        selectTransactionTypeViewModel = ViewModelProvider(requireActivity()).get(SelectTransactionTypeViewModel::class.java)
        if(viewModel.navigatedToSet == TransactionCategoryViewModel.NAVIGATED_TO_SET.TRANSACTION_TYPE
            && selectTransactionTypeViewModel.selectedRecord != null) {
            viewModel.transactionType.value = selectTransactionTypeViewModel.selectedRecord
        }
        println(selectTransactionTypeViewModel.selectedRecord)
        setCategoryListForType(view)
        viewModel.transactionType.observe(this, Observer {
            setCategoryListForType(view)
        })
        val button = view.findViewById<Button>(R.id.transaction_type_button);
        updateTransactionTypeView(button);

        if(args.selectCategory) {
            viewModel.transactionType.value = TransactionType.from(args.transactionType)
            view.findViewById<View>(R.id.card_filter).visibility = View.GONE
            view.findViewById<View>(R.id.create_button).visibility = View.GONE
        } else {
            button.setOnClickListener {
                viewModel.navigatedToSet =
                    TransactionCategoryViewModel.NAVIGATED_TO_SET.TRANSACTION_TYPE
                val action =
                    TransactionCategoryDirections.actionTransactionCategoryToSelectTransactionType()
                findNavController().navigate(action)
            }
            view.findViewById<View>(R.id.create_button)
                .setOnClickListener {
                    val action =
                        TransactionCategoryDirections.actionTransactionCategoryToEditCategory(null)
                    action.transactionType = viewModel.transactionType.value!!.typeCode
                    findNavController().navigate(action)
                }
        }
    }

}

class CategoryListAdapter(
    private val categories: List<Category>,
    private val viewModel: SelectCategoryViewModel,
    private val parentView: View,
    private val selectCategory: Boolean
) : CommonSelectRecyclerListAdapter<CategoryListAdapter.ViewHolder, Category, SelectCategoryViewModel>(categories, viewModel, parentView) {
    lateinit var context: Context

    class ViewHolder(view: View) : CommonSelectViewHolder(view) {
        val label: TextView = view.findViewById(R.id.category_label)
        override fun getDrawableElement(): TextView {
            return label
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
        holder.itemView.background = ShpaeUtil.getRoundCornerShape(15.toFloat(),  Color.WHITE, ContextCompat.getColor(context, type.color))
        if(selectCategory) {
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