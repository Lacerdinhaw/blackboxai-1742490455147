package com.example.churrasquinhoapp.ui.activities

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.churrasquinhoapp.database.AppDatabase
import com.example.churrasquinhoapp.databinding.ActivityAddEditItemBinding
import com.example.churrasquinhoapp.model.Item
import com.example.churrasquinhoapp.repository.InventoryRepository
import com.example.churrasquinhoapp.viewmodel.InventoryViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.*

class AddEditItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditItemBinding
    private var editingItem: Item? = null

    private val viewModel: InventoryViewModel by viewModels {
        val database = AppDatabase.getDatabase(applicationContext)
        InventoryViewModel.Factory(
            InventoryRepository(database.itemDao(), database.saleDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupInputListeners()
        loadItemForEditing()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }

        // Set title based on whether we're adding or editing
        supportActionBar?.title = if (intent.hasExtra("item_id")) {
            "Editar Item"
        } else {
            "Novo Item"
        }
    }

    private fun setupInputListeners() {
        // Add text change listeners for price formatting
        binding.etCostPrice.addTextChangedListener(afterTextChanged = { text ->
            if (!text.isNullOrEmpty() && !text.toString().startsWith("R$")) {
                try {
                    val number = text.toString().toDouble()
                    val formatted = NumberFormat
                        .getCurrencyInstance(Locale("pt", "BR"))
                        .format(number)
                    binding.etCostPrice.setText(formatted)
                    binding.etCostPrice.setSelection(formatted.length)
                } catch (e: NumberFormatException) {
                    binding.tilCostPrice.error = "Valor inválido"
                }
            }
        })

        binding.etSellingPrice.addTextChangedListener(afterTextChanged = { text ->
            if (!text.isNullOrEmpty() && !text.toString().startsWith("R$")) {
                try {
                    val number = text.toString().toDouble()
                    val formatted = NumberFormat
                        .getCurrencyInstance(Locale("pt", "BR"))
                        .format(number)
                    binding.etSellingPrice.setText(formatted)
                    binding.etSellingPrice.setSelection(formatted.length)
                } catch (e: NumberFormatException) {
                    binding.tilSellingPrice.error = "Valor inválido"
                }
            }
        })

        // Save button click listener
        binding.btnSave.setOnClickListener {
            saveItem()
        }
    }

    private fun loadItemForEditing() {
        val itemId = intent.getLongExtra("item_id", -1)
        if (itemId != -1L) {
            viewModel.getItemById(itemId).observe(this) { result ->
                result.onSuccess { item ->
                    editingItem = item
                    populateFields(item)
                }.onFailure {
                    showSnackbar("Erro ao carregar item")
                    finish()
                }
            }
        }
    }

    private fun populateFields(item: Item) {
        binding.apply {
            etItemName.setText(item.name)
            etQuantity.setText(item.quantity.toString())
            etCostPrice.setText(formatCurrency(item.costPrice))
            etSellingPrice.setText(formatCurrency(item.sellingPrice))
            etMinimumStock.setText(item.minimumStock.toString())
            etUnit.setText(item.unit)
        }
    }

    private fun saveItem() {
        // Validate inputs
        if (!validateInputs()) {
            return
        }

        val name = binding.etItemName.text.toString()
        val quantity = binding.etQuantity.text.toString().toInt()
        val costPrice = parseCurrency(binding.etCostPrice.text.toString())
        val sellingPrice = parseCurrency(binding.etSellingPrice.text.toString())
        val minimumStock = binding.etMinimumStock.text.toString().toInt()
        val unit = binding.etUnit.text.toString()

        if (editingItem != null) {
            // Update existing item
            val updatedItem = editingItem!!.copy(
                name = name,
                quantity = quantity,
                costPrice = costPrice,
                sellingPrice = sellingPrice,
                minimumStock = minimumStock,
                unit = unit
            )
            viewModel.updateItem(updatedItem)
        } else {
            // Add new item
            viewModel.addItem(
                name = name,
                quantity = quantity,
                costPrice = costPrice,
                sellingPrice = sellingPrice,
                minimumStock = minimumStock,
                unit = unit
            )
        }

        // Observe the result
        viewModel.operationStatus.observe(this) { status ->
            when (status) {
                is InventoryViewModel.OperationStatus.Success -> {
                    showSnackbar(status.message)
                    finish()
                }
                is InventoryViewModel.OperationStatus.Error -> {
                    showSnackbar(status.message)
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate name
        if (binding.etItemName.text.isNullOrBlank()) {
            binding.tilItemName.error = "Nome é obrigatório"
            isValid = false
        } else {
            binding.tilItemName.error = null
        }

        // Validate quantity
        try {
            val quantity = binding.etQuantity.text.toString().toInt()
            if (quantity < 0) {
                binding.tilQuantity.error = "Quantidade deve ser maior ou igual a zero"
                isValid = false
            } else {
                binding.tilQuantity.error = null
            }
        } catch (e: NumberFormatException) {
            binding.tilQuantity.error = "Quantidade inválida"
            isValid = false
        }

        // Validate prices
        val costPrice = parseCurrency(binding.etCostPrice.text.toString())
        val sellingPrice = parseCurrency(binding.etSellingPrice.text.toString())

        if (costPrice <= 0) {
            binding.tilCostPrice.error = "Preço de custo deve ser maior que zero"
            isValid = false
        } else {
            binding.tilCostPrice.error = null
        }

        if (sellingPrice <= costPrice) {
            binding.tilSellingPrice.error = "Preço de venda deve ser maior que o preço de custo"
            isValid = false
        } else {
            binding.tilSellingPrice.error = null
        }

        // Validate minimum stock
        try {
            val minStock = binding.etMinimumStock.text.toString().toInt()
            if (minStock < 0) {
                binding.tilMinimumStock.error = "Estoque mínimo deve ser maior ou igual a zero"
                isValid = false
            } else {
                binding.tilMinimumStock.error = null
            }
        } catch (e: NumberFormatException) {
            binding.tilMinimumStock.error = "Valor inválido"
            isValid = false
        }

        // Validate unit
        if (binding.etUnit.text.isNullOrBlank()) {
            binding.tilUnit.error = "Unidade é obrigatória"
            isValid = false
        } else {
            binding.tilUnit.error = null
        }

        return isValid
    }

    private fun formatCurrency(value: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)
    }

    private fun parseCurrency(value: String): Double {
        return try {
            value.replace(Regex("[R$,.]"), "").toDouble() / 100
        } catch (e: NumberFormatException) {
            0.0
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}