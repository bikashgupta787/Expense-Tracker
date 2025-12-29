package com.example.expensetracker.Adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expensetracker.CategoryUiConfig
import com.example.expensetracker.data_class.Transaction
import com.example.expensetracker.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//class TransactionAdapter(
//    private val transactions: List<Transaction>
//) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
//
//    inner class TransactionViewHolder(val binding: ItemTransactionBinding) :
//        RecyclerView.ViewHolder(binding.root)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
//        val binding = ItemTransactionBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return TransactionViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
//        val transaction = transactions[position]
//        with(holder.binding) {
//            tvTitle.text = transaction.title
//            tvCategory.text = transaction.category
//
//            tvDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
//                .format(Date(transaction.date))
//
//            if (transaction.type == "Income") {
//                tvAmount.text = "+ ₹${transaction.amount}"
//                tvAmount.setTextColor(root.context.getColor(android.R.color.holo_green_dark))
//            } else {
//                tvAmount.text = "- ₹${transaction.amount}"
//                tvAmount.setTextColor(root.context.getColor(android.R.color.holo_red_dark))
//            }
//        }
//    }
//
//    override fun getItemCount(): Int = transactions.size
//}



class TransactionAdapter :
    ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(DiffCallback) {

    inner class TransactionViewHolder(val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        with(holder.binding) {
            tvTitle.text = transaction.title
            tvCategory.text = transaction.category

            tvDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(Date(transaction.date))

            if (transaction.type == "Income") {
                tvAmount.text = "+ ₹${transaction.amount}"
                tvAmount.setTextColor(root.context.getColor(android.R.color.holo_green_dark))
            } else {
                tvAmount.text = "- ₹${transaction.amount}"
                tvAmount.setTextColor(root.context.getColor(android.R.color.holo_red_dark))
            }

            holder.binding.ivCategoryIcon.setImageResource(
                CategoryUiConfig.iconRes(transaction.category)
            )

            // CATEGORY COLOR
            val colorRes = CategoryUiConfig.colorRes(transaction.category)
            val color = ContextCompat.getColor(holder.itemView.context, colorRes)

            holder.binding.ivCategoryIcon.backgroundTintList =
                ColorStateList.valueOf(color)
        }
    }


//    private fun getCategoryIcon(category: String): Int {
//        return when (category.lowercase()) {
//            "food" -> R.drawable.ic_food
//            "shopping" -> R.drawable.ic_shopping
//            "transport" -> R.drawable.ic_transport
//            "living" -> R.drawable.ic_living
//            "salary" -> R.drawable.ic_work
//            "bills" -> R.drawable.ic_wallet
//            "entertainment" -> R.drawable.ic_entertainment
//            "health" -> R.drawable.ic_health
//            "education" -> R.drawable.ic_education
//            else -> R.drawable.ic_category
//        }
//    }
//
//    private fun getCategoryColorRes(category: String): Int {
//        return when (category.lowercase()) {
//            "food" -> R.color.cat_food
//            "shopping" -> R.color.cat_shopping
//            "transport" -> R.color.cat_transport
//            "living" -> R.color.cat_living
//            "health" -> R.color.cat_health
//            "bills" -> R.color.cat_bills
//            "entertainment" -> R.color.cat_entertainment
//            "salary" -> R.color.cat_salary
//            "bonus" -> R.color.cat_bonus
//            else -> R.color.cat_other
//        }
//    }





    companion object DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id   // MUST use primary key
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}
