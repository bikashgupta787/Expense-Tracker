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


    companion object DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem.id == newItem.id   // MUST use primary key
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem == newItem
        }
    }
}
