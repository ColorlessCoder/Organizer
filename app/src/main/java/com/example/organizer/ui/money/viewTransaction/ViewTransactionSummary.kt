package com.example.organizer.ui.money.viewTransaction

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.organizer.R
import com.example.organizer.database.enums.DebtType
import com.example.organizer.database.enums.TransactionType
import com.example.organizer.database.relation.TransactionDetails
import com.example.organizer.ui.Utils.StringUtils.Companion.doubleToString
import com.example.organizer.ui.money.viewTransaction.ViewTransactionSummaryViewModel.Companion.NO_CATEGORY


class ViewTransactionSummary : Fragment() {

    companion object {
        fun newInstance() = ViewTransactionSummary()
    }

    private lateinit var viewModel: ViewTransactionSummaryViewModel
    private lateinit var simpleTransactionListViewModel: SimpleTransactionListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.view_transaction_summary_fragment, container, false)
    }

    private fun getDebtWiseSummary(): List<DebtSummaryRowData> {
        val debtSummaryMap = mutableMapOf<String, DebtSummaryRowData>()
        viewModel.getTransactions()
            .filter { it.debt != null }
            .forEach {
                val transaction = it.transaction
                if (!debtSummaryMap.containsKey(transaction.debtId)) {
                    debtSummaryMap[transaction.debtId!!] = DebtSummaryRowData(
                        DebtType.from(it.debt!!.debtType).name,
                        it.debt.details,
                        0.0,
                        0.0,
                        0,
                        it.debt.id
                    )
                }
                val debtSummaryRowData = debtSummaryMap[transaction.debtId!!]
                if (debtSummaryRowData != null) {
                    if (it.transaction.transactionType == TransactionType.EXPENSE.typeCode) {
                        debtSummaryRowData.expense += transaction.amount
                        debtSummaryRowData.numberOfTransactions++;
                    } else if ((it.transaction.transactionType == TransactionType.INCOME.typeCode)) {
                        debtSummaryRowData.income += transaction.amount
                        debtSummaryRowData.numberOfTransactions++;
                    }
                }
            }
        return debtSummaryMap.entries.map { it.value }
            .sortedWith(compareBy(DebtSummaryRowData::type, DebtSummaryRowData::details))
    }

    private fun getAccountWiseSummary(): List<AccountSummaryRowData> {
        val accountSummaryMap = mutableMapOf<String, AccountSummaryRowData>()
        viewModel.getTransactions().forEach {
            val transaction = it.transaction
            var fromAccount: AccountSummaryRowData? = null
            if (it.fromAccountName != null) {
                if (!accountSummaryMap.containsKey(it.fromAccountName)) {
                    accountSummaryMap[it.fromAccountName] =
                        AccountSummaryRowData(
                            it.fromAccountName,
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                            0,
                            transaction.fromAccount!!
                        )
                }
                fromAccount = accountSummaryMap.get(it.fromAccountName)
            }
            var toAccount: AccountSummaryRowData? = null
            if (it.toAccountName != null) {
                if (!accountSummaryMap.containsKey(it.toAccountName)) {
                    accountSummaryMap[it.toAccountName] =
                        AccountSummaryRowData(
                            it.toAccountName,
                            0.0,
                            0.0,
                            0.0,
                            0.0,
                            0,
                            transaction.toAccount!!
                        )
                }
                toAccount = accountSummaryMap.get(it.toAccountName)
            }
            if (it.transaction.transactionType == TransactionType.EXPENSE.typeCode && fromAccount != null) {
                fromAccount.expense += transaction.amount
                fromAccount.numberOfTransactions++;
            } else if ((it.transaction.transactionType == TransactionType.INCOME.typeCode) && toAccount != null) {
                toAccount.income += transaction.amount
                toAccount.numberOfTransactions++;
            } else if (it.transaction.transactionType == TransactionType.TRANSFER.typeCode) {
                if (toAccount != null) {
                    toAccount.transferIn += transaction.amount
                    toAccount.numberOfTransactions++;
                }
                if (fromAccount != null) {
                    fromAccount.transferOut += transaction.amount
                    fromAccount.numberOfTransactions++;
                }
            }
        }
        return accountSummaryMap.map { it.value }.sortedBy { it.name }
    }

    data class CategoryGroupTreeNode(
        val value: CategorySummaryRowData,
        val children: MutableMap<String, CategoryGroupTreeNode>
    )

    private fun insertUpdateCategorySummary(
        currentNode: CategoryGroupTreeNode,
        currentCategory: CategorySummaryRowData,
        transaction: TransactionDetails,
        level: Int
    ) {
        var group = currentCategory.name.trim()
        val firstGroupKeyIndex = currentCategory.name.indexOfFirst { it == ':' }
        if (firstGroupKeyIndex != -1) {
            group = currentCategory.name.substring(0, firstGroupKeyIndex).trim()
        }
        if (level != -1 || group.isEmpty()) {
            currentNode.value.total += currentCategory.total
            currentNode.value.numberOfTransactions++
            currentNode.value.idSet.addAll(currentCategory.idSet)
            currentNode.value.leaf = currentNode.value.leaf && group.isEmpty()
        }

        if (group.isNotEmpty()) {
            currentCategory.name =
                if (firstGroupKeyIndex != -1) currentCategory.name.substring(firstGroupKeyIndex + 1)
                    .trim() else ""
            if (!currentNode.children.contains(group)) {
                currentNode.children[group] = CategoryGroupTreeNode(
                    CategorySummaryRowData(
                        level + 1,
                        group,
                        0.0,
                        currentCategory.color,
                        0,
                        mutableSetOf(),
                        true,
                        mutableListOf()
                    ),
                    mutableMapOf()
                )
            }
            insertUpdateCategorySummary(
                currentNode.children[group]!!,
                currentCategory,
                transaction,
                level + 1
            )
        } else {
            currentNode.value.transactions.add(transaction)
        }
    }

    private fun traverseCategorySummaryTree(
        currentNode: CategoryGroupTreeNode,
        result: MutableList<CategoryGroupTreeNode>,
        level: Int
    ) {
        currentNode.children
            .entries
            .sortedBy { it.key }
            .map { it.value }
            .forEach {
                result.add(it)
                traverseCategorySummaryTree(it, result, level + 1)
            }
        if (level == -1) {
            result.add(currentNode)
        }
    }

    private fun createBlankSummaryRowData(
        name: String?,
        amount: Double,
        color: Int
    ): CategorySummaryRowData {
        return CategorySummaryRowData(
            0,
            name ?: "",
            amount,
            color,
            0,
            mutableSetOf(),
            true,
            mutableListOf()
        )
    }

    private fun getCategoryWiseSummary(): List<CategoryGroupTreeNode> {
        val incomeColor = ContextCompat.getColor(requireContext(), TransactionType.INCOME.color)
        val expenseColor = ContextCompat.getColor(requireContext(), TransactionType.EXPENSE.color)
        val transferColor = ContextCompat.getColor(requireContext(), TransactionType.TRANSFER.color)
        val rootCategoryIncomeSummaryNode = CategoryGroupTreeNode(
            createBlankSummaryRowData(NO_CATEGORY, 0.0, incomeColor),
            mutableMapOf()
        )
        val rootCategoryExpenseSummaryNode = CategoryGroupTreeNode(
            createBlankSummaryRowData(NO_CATEGORY, 0.0, expenseColor),
            mutableMapOf()
        )
        val rootCategoryTransferSummaryNode = CategoryGroupTreeNode(
            createBlankSummaryRowData(NO_CATEGORY, 0.0, transferColor),
            mutableMapOf()
        )
        viewModel.getTransactions()
            .filter { it.debt == null }
            .forEach {
                val transaction = it.transaction
                if (it.transaction.transactionType == TransactionType.EXPENSE.typeCode) {
                    insertUpdateCategorySummary(
                        rootCategoryExpenseSummaryNode,
                        createBlankSummaryRowData(
                            it.categoryName,
                            transaction.amount,
                            expenseColor
                        ),
                        it,
                        -1
                    )
                } else if (it.transaction.transactionType == TransactionType.INCOME.typeCode) {
                    insertUpdateCategorySummary(
                        rootCategoryIncomeSummaryNode,
                        createBlankSummaryRowData(it.categoryName, transaction.amount, incomeColor),
                        it,
                        -1
                    )
                } else if (it.transaction.transactionType == TransactionType.TRANSFER.typeCode) {
                    insertUpdateCategorySummary(
                        rootCategoryTransferSummaryNode,
                        createBlankSummaryRowData(
                            it.categoryName,
                            transaction.amount,
                            transferColor
                        ),
                        it,
                        -1
                    )
                }
            }
        val categorySummaryRowDataList = mutableListOf<CategoryGroupTreeNode>()
        traverseCategorySummaryTree(rootCategoryExpenseSummaryNode, categorySummaryRowDataList, -1)
        traverseCategorySummaryTree(rootCategoryIncomeSummaryNode, categorySummaryRowDataList, -1)
        traverseCategorySummaryTree(rootCategoryTransferSummaryNode, categorySummaryRowDataList, -1)
        return categorySummaryRowDataList
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel =
            ViewModelProvider(requireActivity()).get(ViewTransactionSummaryViewModel::class.java)
        viewModel.layoutLoaded.value = 0
        simpleTransactionListViewModel =
            ViewModelProvider(requireActivity()).get(SimpleTransactionListViewModel::class.java)
        if (viewModel.accountSummaryList.isEmpty()) {
            viewModel.accountSummaryList = getAccountWiseSummary().toMutableList()
        }
        if (viewModel.categorySummaryList.isEmpty()) {
            viewModel.categorySummaryList = getCategoryWiseSummary().toMutableList()
        }
        if (viewModel.debSummaryList.isEmpty()) {
            viewModel.debSummaryList = getDebtWiseSummary().toMutableList()
        }

        val accountSummaryView: RecyclerView = view.findViewById(R.id.account_summary_list)
        accountSummaryView.adapter =
            AccountSummaryAdapter(
                viewModel.accountSummaryList,
                view,
                viewModel,
                simpleTransactionListViewModel
            )
        val categorySummaryView: RecyclerView = view.findViewById(R.id.category_summary_list)
        categorySummaryView.adapter =
            CategorySummaryAdapter(
                viewModel.categorySummaryList,
                view,
                viewModel,
                simpleTransactionListViewModel
            )
        val debtSummaryView: RecyclerView = view.findViewById(R.id.debt_summary_list)
        debtSummaryView.adapter =
            DebtSummaryAdapter(
                viewModel.debSummaryList,
                view,
                viewModel,
                simpleTransactionListViewModel
            )
        viewModel.layoutLoaded.observe(viewLifecycleOwner, Observer {
            if(it != null && viewModel.focusedRow != null && it == ViewTransactionSummaryViewModel.NUMBER_OF_LAYOUT) {
                val scrollView = view.findViewById<NestedScrollView>(R.id.SummaryNestedScrollView)
                scrollView.post { scrollView.scrollTo(0, viewModel.focusedRow!!.bottom) }
            }
        })
        categorySummaryView.layoutManager = object: LinearLayoutManager(
            requireActivity(),
            VERTICAL,
            false
        ) {
            override fun onLayoutCompleted(state: RecyclerView.State?) {
                super.onLayoutCompleted(state)
                viewModel.layoutLoaded.value = (viewModel.layoutLoaded.value?:0 ) + 1
            }
        }
        accountSummaryView.layoutManager = object: LinearLayoutManager(
            requireActivity(),
            VERTICAL,
            false
        ) {
            override fun onLayoutCompleted(state: RecyclerView.State?) {
                super.onLayoutCompleted(state)
                viewModel.layoutLoaded.value = (viewModel.layoutLoaded.value?:0 ) + 1
            }
        }
        debtSummaryView.layoutManager = object: LinearLayoutManager(
            requireActivity(),
            VERTICAL,
            false
        ) {
            override fun onLayoutCompleted(state: RecyclerView.State?) {
                super.onLayoutCompleted(state)
                viewModel.layoutLoaded.value = (viewModel.layoutLoaded.value?:0 ) + 1
            }
        }
    }

}

data class AccountSummaryRowData(
    val name: String,
    var expense: Double,
    var income: Double,
    var transferIn: Double,
    var transferOut: Double,
    var numberOfTransactions: Int,
    val id: String
)

data class CategorySummaryRowData(
    var level: Int,
    var name: String,
    var total: Double,
    var color: Int,
    var numberOfTransactions: Int,
    var idSet: MutableSet<String>,
    var leaf: Boolean,
    var transactions: MutableList<TransactionDetails>
)

data class DebtSummaryRowData(
    var type: String,
    var details: String,
    var income: Double,
    var expense: Double,
    var numberOfTransactions: Int,
    var id: String
)

class AccountSummaryAdapter(
    private val transactionDetailsList: List<AccountSummaryRowData>,
    val view: View,
    val viewModel: ViewTransactionSummaryViewModel,
    private val simpleTransactionListViewModel: SimpleTransactionListViewModel
) : RecyclerView.Adapter<AccountSummaryAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val account_summary_row_name: TextView = view.findViewById(R.id.account_summary_row_name)
        val account_summary_row_income: TextView =
            view.findViewById(R.id.account_summary_row_income)
        val account_summary_row_expense: TextView =
            view.findViewById(R.id.account_summary_row_expense)
        val account_summary_row_count: TextView = view.findViewById(R.id.account_summary_row_count)
        val account_summary_row_transfer_in: TextView =
            view.findViewById(R.id.account_summary_row_transfer_in)
        val account_summary_row_transfer_out: TextView =
            view.findViewById(R.id.account_summary_row_transfer_out)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.account_summary_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return transactionDetailsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transactionDetails = transactionDetailsList.get(position)
        holder.account_summary_row_name.text = transactionDetails.name;
        holder.account_summary_row_income.text = doubleToString(transactionDetails.income)
        holder.account_summary_row_expense.text = doubleToString(transactionDetails.expense)
        holder.account_summary_row_count.text =
            transactionDetails.numberOfTransactions.toString() + if (transactionDetails.numberOfTransactions > 1) " Transactions" else "Transaction";
        holder.account_summary_row_transfer_in.text = doubleToString(transactionDetails.transferIn)
        holder.account_summary_row_transfer_out.text =
            doubleToString(transactionDetails.transferOut)
        holder.itemView.setOnClickListener {
            viewModel.focusedRow = holder.itemView
            simpleTransactionListViewModel.transactionList = viewModel.getAllTransactionsForAccount(transactionDetails.name)
            val action = ViewTransactionSummaryDirections.actionViewTransactionSummaryToSimpleTransactionList()
            view.findNavController().navigate(action)
        }
    }

}

class CategorySummaryAdapter(
    private val transactionDetailsList: List<ViewTransactionSummary.CategoryGroupTreeNode>,
    val view: View,
    val viewModel: ViewTransactionSummaryViewModel,
    private val simpleTransactionListViewModel: SimpleTransactionListViewModel
) : RecyclerView.Adapter<CategorySummaryAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val category_summary_row_name: TextView = view.findViewById(R.id.category_summary_row_name)
        val category_summary_row_total: TextView =
            view.findViewById(R.id.category_summary_row_total)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_summary_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return transactionDetailsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transactionDetailsNode = transactionDetailsList.get(position)
        val transactionDetails = transactionDetailsNode.value
        holder.category_summary_row_name.text =
            "     ".repeat(transactionDetails.level) + transactionDetails.name;
        holder.category_summary_row_total.text = doubleToString(transactionDetails.total)
        holder.category_summary_row_total.setTextColor(transactionDetails.color)
        if (transactionDetails.leaf) {
            holder.category_summary_row_total.setTypeface(
                holder.category_summary_row_total.typeface,
                Typeface.BOLD
            )
            holder.category_summary_row_name.setTypeface(
                holder.category_summary_row_total.typeface,
                Typeface.BOLD
            )
        }
        holder.itemView.setOnClickListener {
            viewModel.focusedRow = holder.itemView
            simpleTransactionListViewModel.transactionList.clear()
            viewModel.getAllTransactionsUnderCategory(transactionDetailsNode, simpleTransactionListViewModel.transactionList)
            val action = ViewTransactionSummaryDirections.actionViewTransactionSummaryToSimpleTransactionList()
            view.findNavController().navigate(action)
        }
    }

}

class DebtSummaryAdapter(
    private val transactionDetailsList: List<DebtSummaryRowData>,
    val view: View,
    val viewModel: ViewTransactionSummaryViewModel,
    private val simpleTransactionListViewModel: SimpleTransactionListViewModel
) : RecyclerView.Adapter<DebtSummaryAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var debt_summary_row_type: TextView = view.findViewById(R.id.debt_summary_row_type)
        var debt_summary_row_income: TextView = view.findViewById(R.id.debt_summary_row_income)
        var debt_summary_row_expense: TextView = view.findViewById(R.id.debt_summary_row_expense)
        var debt_summary_row_details: TextView = view.findViewById(R.id.debt_summary_row_details)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.debt_summary_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return transactionDetailsList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transactionDetails = transactionDetailsList.get(position)
        holder.debt_summary_row_type.text = transactionDetails.type
        holder.debt_summary_row_details.text = transactionDetails.details
        holder.debt_summary_row_income.text = doubleToString(transactionDetails.income)
        holder.debt_summary_row_expense.text = doubleToString(transactionDetails.expense)
        holder.itemView.setOnClickListener {
            viewModel.focusedRow = holder.itemView
            simpleTransactionListViewModel.transactionList = viewModel.getAllTransactionsForDebt(transactionDetails.id)
            val action = ViewTransactionSummaryDirections.actionViewTransactionSummaryToSimpleTransactionList()
            view.findNavController().navigate(action)
        }
    }

}