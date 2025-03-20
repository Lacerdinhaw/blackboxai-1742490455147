package com.example.churrasquinhoapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.churrasquinhoapp.databinding.ItemSaleBinding
import com.example.churrasquinhoapp.model.Sale
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class SalesAdapter : ListAdapter<SaleWithItemInfo, SalesAdapter.SaleViewHolder>(SaleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val binding = ItemSaleBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SaleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SaleViewHolder(
        private val binding: ItemSaleBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

        fun bind(saleWithInfo: SaleWithItemInfo) {
            binding.apply {
                tvItemName.text = saleWithInfo.itemName
                tvSaleDate.text = dateFormat.format(saleWithInfo.sale.saleDate)
                tvQuantity.text = "Quantidade: ${saleWithInfo.sale.quantity} ${saleWithInfo.itemUnit}"
                tvUnitPrice.text = "Preço unitário: ${currencyFormat.format(saleWithInfo.itemPrice)}/${saleWithInfo.itemUnit}"
                tvTotalValue.text = "Total: ${currencyFormat.format(saleWithInfo.sale.totalValue)}"

                // Show additional info if available
                if (saleWithInfo.additionalInfo.isNotEmpty()) {
                    tvAdditionalInfo.text = saleWithInfo.additionalInfo
                    tvAdditionalInfo.visibility = android.view.View.VISIBLE
                    divider.visibility = android.view.View.VISIBLE
                } else {
                    tvAdditionalInfo.visibility = android.view.View.GONE
                    divider.visibility = android.view.View.GONE
                }
            }
        }
    }

    private class SaleDiffCallback : DiffUtil.ItemCallback<SaleWithItemInfo>() {
        override fun areItemsTheSame(oldItem: SaleWithItemInfo, newItem: SaleWithItemInfo): Boolean {
            return oldItem.sale.id == newItem.sale.id
        }

        override fun areContentsTheSame(oldItem: SaleWithItemInfo, newItem: SaleWithItemInfo): Boolean {
            return oldItem == newItem
        }
    }
}

// Data class to hold sale information along with related item details
data class SaleWithItemInfo(
    val sale: Sale,
    val itemName: String,
    val itemUnit: String,
    val itemPrice: Double,
    val additionalInfo: String = ""
)