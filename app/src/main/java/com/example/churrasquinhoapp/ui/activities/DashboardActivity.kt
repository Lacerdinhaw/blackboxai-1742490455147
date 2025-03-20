package com.example.churrasquinhoapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.churrasquinhoapp.database.AppDatabase
import com.example.churrasquinhoapp.databinding.ActivityDashboardBinding
import com.example.churrasquinhoapp.repository.InventoryRepository
import com.example.churrasquinhoapp.ui.adapters.LowStockAdapter
import com.example.churrasquinhoapp.viewmodel.InventoryViewModel
import com.example.churrasquinhoapp.viewmodel.SalesViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var lowStockAdapter: LowStockAdapter

    private val inventoryViewModel: InventoryViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        InventoryViewModel.Factory(
            InventoryRepository(database.itemDao(), database.saleDao())
        )
    }

    private val salesViewModel: SalesViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        SalesViewModel.Factory(
            InventoryRepository(database.itemDao(), database.saleDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
        setupClickListeners()
    }

    private fun setupUI() {
        // Configure RecyclerView for low stock items
        lowStockAdapter = LowStockAdapter { item ->
            // Navigate to inventory with the selected item
            val intent = Intent(this, InventoryActivity::class.java).apply {
                putExtra("selected_item_id", item.id)
            }
            startActivity(intent)
        }

        binding.rvLowStock.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = lowStockAdapter
        }

        // Update sales statistics
        salesViewModel.updateSalesStats()
    }

    private fun setupObservers() {
        // Observe low stock items
        inventoryViewModel.lowStockItems.observe(this) { items ->
            lowStockAdapter.submitList(items)
            binding.tvNoLowStock.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        }

        // Observe sales statistics
        salesViewModel.salesStats.observe(this) { stats ->
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
            binding.tvTotalSales.text = "Vendas hoje: ${stats.count}"
            binding.tvTotalRevenue.text = "Receita: ${currencyFormat.format(stats.totalValue)}"
        }

        // Observe operation status for both ViewModels
        inventoryViewModel.operationStatus.observe(this) { status ->
            when (status) {
                is InventoryViewModel.OperationStatus.Error -> {
                    showSnackbar(status.message)
                }
                else -> {} // Success messages handled in specific operations
            }
        }

        salesViewModel.operationStatus.observe(this) { status ->
            when (status) {
                is SalesViewModel.OperationStatus.Error -> {
                    showSnackbar(status.message)
                }
                else -> {} // Success messages handled in specific operations
            }
        }
    }

    private fun setupClickListeners() {
        // Navigate to Inventory
        binding.btnInventory.setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }

        // Navigate to Sales
        binding.btnSales.setOnClickListener {
            startActivity(Intent(this, SalesActivity::class.java))
        }

        // Add new sale
        binding.fabAddSale.setOnClickListener {
            startActivity(Intent(this, AddSaleActivity::class.java))
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh sales statistics when returning to the dashboard
        salesViewModel.updateSalesStats()
    }
}