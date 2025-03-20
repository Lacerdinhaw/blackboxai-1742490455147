package com.example.churrasquinhoapp.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.churrasquinhoapp.database.AppDatabase
import com.example.churrasquinhoapp.databinding.ActivityInventoryBinding
import com.example.churrasquinhoapp.repository.InventoryRepository
import com.example.churrasquinhoapp.ui.adapters.InventoryAdapter
import com.example.churrasquinhoapp.viewmodel.InventoryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

class InventoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryBinding
    private lateinit var inventoryAdapter: InventoryAdapter

    private val viewModel: InventoryViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        InventoryViewModel.Factory(
            InventoryRepository(database.itemDao(), database.saleDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        // Check if we should scroll to a specific item
        intent.getLongExtra("selected_item_id", -1).takeIf { it != -1L }?.let { itemId ->
            viewModel.getItemById(itemId).observe(this) { result ->
                result.onSuccess { item ->
                    inventoryAdapter.highlightItem(item)
                }
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupRecyclerView() {
        inventoryAdapter = InventoryAdapter(
            onItemClick = { item ->
                // Navigate to edit item screen
                Intent(this, AddEditItemActivity::class.java).apply {
                    putExtra("item_id", item.id)
                    startActivity(this)
                }
            },
            onDeleteClick = { item ->
                showDeleteConfirmationDialog(item.name) {
                    viewModel.deleteItem(item)
                }
            }
        )

        binding.rvInventory.apply {
            layoutManager = LinearLayoutManager(this@InventoryActivity)
            adapter = inventoryAdapter
        }
    }

    private fun setupObservers() {
        viewModel.allItems.observe(this) { items ->
            inventoryAdapter.submitList(items)
            binding.tvEmptyInventory.visibility = 
                if (items.isEmpty()) android.view.View.VISIBLE 
                else android.view.View.GONE
        }

        viewModel.operationStatus.observe(this) { status ->
            when (status) {
                is InventoryViewModel.OperationStatus.Success -> {
                    showSnackbar(status.message)
                }
                is InventoryViewModel.OperationStatus.Error -> {
                    showSnackbar(status.message)
                }
            }
        }
    }

    private fun setupClickListeners() {
        binding.fabAddItem.setOnClickListener {
            startActivity(Intent(this, AddEditItemActivity::class.java))
        }
    }

    private fun showDeleteConfirmationDialog(itemName: String, onConfirm: () -> Unit) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Confirmar exclusão")
            .setMessage("Deseja realmente excluir o item '$itemName'?")
            .setPositiveButton("Excluir") { _, _ -> onConfirm() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}