package com.example.churrasquinhoapp.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.churrasquinhoapp.database.AppDatabase
import com.example.churrasquinhoapp.databinding.ActivitySalesBinding
import com.example.churrasquinhoapp.repository.InventoryRepository
import com.example.churrasquinhoapp.ui.adapters.SalesAdapter
import com.example.churrasquinhoapp.viewmodel.SalesViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import java.util.*

class SalesActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalesBinding
    private lateinit var salesAdapter: SalesAdapter

    private val viewModel: SalesViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        SalesViewModel.Factory(
            InventoryRepository(database.itemDao(), database.saleDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySalesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupRecyclerView() {
        salesAdapter = SalesAdapter()
        binding.rvSales.apply {
            layoutManager = LinearLayoutManager(this@SalesActivity)
            adapter = salesAdapter
        }
    }

    private fun setupObservers() {
        // Observe sales list
        viewModel.allSales.observe(this) { sales ->
            salesAdapter.submitList(sales)
            binding.tvEmptySales.visibility = 
                if (sales.isEmpty()) android.view.View.VISIBLE 
                else android.view.View.GONE
        }

        // Observe sales statistics
        viewModel.salesStats.observe(this) { stats ->
            binding.tvTotalSales.text = "Total de vendas: ${stats.count}"
            binding.tvTotalRevenue.text = "Receita total: R$ ${String.format("%.2f", stats.totalValue)}"
        }

        // Observe operation status
        viewModel.operationStatus.observe(this) { status ->
            when (status) {
                is SalesViewModel.OperationStatus.Success -> {
                    showSnackbar(status.message)
                }
                is SalesViewModel.OperationStatus.Error -> {
                    showSnackbar(status.message)
                }
            }
        }
    }

    private fun setupClickListeners() {
        // FAB click to add new sale
        binding.fabAddSale.setOnClickListener {
            startActivity(Intent(this, AddSaleActivity::class.java))
        }

        // Date filter click
        binding.btnDateFilter.setOnClickListener {
            showDateRangePicker()
        }
    }

    private fun showDateRangePicker() {
        val dateRangePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText("Selecione o período")
            .build()

        dateRangePicker.addOnPositiveButtonClickListener { dateRange ->
            val startDate = Date(dateRange.first)
            val endDate = Date(dateRange.second)
            viewModel.updateSalesStats(startDate, endDate)
        }

        dateRangePicker.show(supportFragmentManager, "date_range_picker")
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onResume() {
        super.onResume()
        // Refresh sales statistics
        viewModel.updateSalesStats()
    }
}