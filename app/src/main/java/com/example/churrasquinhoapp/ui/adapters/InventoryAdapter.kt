package com.example.churrasquinhoapp.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.churrasquinhoapp.databinding.ItemInventoryBinding
import com.example.churrasquinhoapp.model.Item
import java.text.NumberFormat
import java.util.*

class InventoryAdapter(
    private val onItemClick: (Item) -> Unit,
    private val onDeleteClick: (Item) -> Unit
) : ListAdapter<Item, InventoryAdapter.InventoryViewHolder>(InventoryDiffCallback()) {

    private var highlightedItemId: Long = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryViewHolder {
        val binding = ItemInventoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InventoryViewHolder(binding, onItemClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: InventoryViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, item.id == highlightedItemId)
        
        // Reset highlight after binding
        if (item.id == highlightedItemId) {
            highlightedItemId = -1
        }
    }

    fun highlightItem(item: Item) {
        val position = currentList.indexOfFirst { it.id == item.id }
        if (position != -1) {
            highlightedItemId = item.id
            notifyItemChanged(position)
        }
    }

    class InventoryViewHolder(
        private val binding: ItemInventoryBinding,
        private val onItemClick: (Item) -> Unit,
        private val onDeleteClick: (Item) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        fun bind(item: Item, isHighlighted: Boolean) {
            binding.apply {
                // Set item details
                tvItemName.text = item.name
                tvQuantity.text = "Quantidade: ${item.quantity} ${item.unit}"
                tvCostPrice.text = "Preço de custo: ${currencyFormat.format(item.costPrice)}/${item.unit}"
                tvSellingPrice.text = "Preço de venda: ${currencyFormat.format(item.sellingPrice)}/${item.unit}"

                // Show warning if stock is low
                tvStockWarning.visibility = 
                    if (item.quantity <= item.minimumStock) android.view.View.VISIBLE 
                    else android.view.View.GONE

                // Highlight item if needed
                root.setCardBackgroundColor(
                    if (isHighlighted) Color.parseColor("#FFF3E0")
                    else Color.WHITE
                )

                // Set click listeners
                root.setOnClickListener { onItemClick(item) }
                btnDelete.setOnClickListener { onDeleteClick(item) }
            }
        }
    }

    private class InventoryDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
}