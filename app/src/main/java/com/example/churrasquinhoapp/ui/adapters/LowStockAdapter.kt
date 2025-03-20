package com.example.churrasquinhoapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.churrasquinhoapp.databinding.ItemLowStockBinding
import com.example.churrasquinhoapp.model.Item

class LowStockAdapter(
    private val onItemClick: (Item) -> Unit
) : ListAdapter<Item, LowStockAdapter.LowStockViewHolder>(LowStockDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LowStockViewHolder {
        val binding = ItemLowStockBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LowStockViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: LowStockViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LowStockViewHolder(
        private val binding: ItemLowStockBinding,
        private val onItemClick: (Item) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.apply {
                tvItemName.text = item.name
                tvCurrentStock.text = "Estoque atual: ${item.quantity} ${item.unit}"
                tvMinimumStock.text = "Mínimo: ${item.minimumStock} ${item.unit}"

                // Setup click listeners
                root.setOnClickListener { onItemClick(item) }
                btnReplenish.setOnClickListener { onItemClick(item) }
            }
        }
    }

    private class LowStockDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
}