package com.example.churrasquinhoapp.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.churrasquinhoapp.database.AppDatabase
import com.example.churrasquinhoapp.databinding.ActivityAddSaleBinding
import com.example.churrasquinhoapp.model.Item
import com.example.churrasquinhoapp.repository.InventoryRepository
import com.example.churrasquinhoapp.viewmodel.InventoryViewModel
import com.example.churrasquinhoapp.viewmodel.SalesViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.NumberFormat
import java.util.*

class AddSaleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddSaleBinding
    private var selectedItem: Item? = null
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

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
        binding = ActivityAddSaleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupItemSelection()
        setupQuantityInput()
        setupPriceInput()
        setupSaveButton()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupItemSelection() {
        inventoryViewModel.allItems.observe(this) { items ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_dropdown_item_1line,
                items.map { it.name }
            )
            binding.actvItem.setAdapter(adapter)

            binding.actvItem.setOnItemClickListener { _, _, position, _ ->
                selectedItem = items[position]
                updateItemInfo()
            }
        }
    }

    private fun setupQuantityInput() {
        binding.etQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                calculateTotal()
            }
        })
    }

    private fun setupPriceInput() {
        binding.etUnitPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty() && !s.toString().startsWith("R$")) {
                    try {
                        val number = s.toString().toDouble()
                        val formatted = currencyFormat.format(number)
                        binding.etUnitPrice.removeTextChangedListener(this)
                        binding.etUnitPrice.setText(formatted)
                        binding.etUnitPrice.setSelection(formatted.length)
                        binding.etUnitPrice.addTextChangedListener(this)
                        calculateTotal()
                    } catch (e: NumberFormatException) {
                        binding.tilUnitPrice.error = "Valor inválido"
                    }
                }
            }
        })
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            if (validateInputs()) {
                registerSale()
            }
        }
    }

    private fun updateItemInfo() {
        selectedItem?.let { item ->
            binding.tvCurrentStock.text = "Estoque atual: ${item.quantity} ${item.unit}"
            binding.etUnitPrice.setText(currencyFormat.format(item.sellingPrice))
            binding.tvUnit.text = item.unit
            
            binding.groupItemInfo.visibility = View.VISIBLE
        }
    }

    private fun calculateTotal() {
        try {
            val quantity = binding.etQuantity.text.toString().toDoubleOrNull() ?: 0.0
            val unitPrice = parseCurrency(binding.etUnitPrice.text.toString())
            val total = quantity * unitPrice
            
            binding.tvTotal.text = "Total: ${currencyFormat.format(total)}"
        } catch (e: NumberFormatException) {
            binding.tvTotal.text = "Total: ${currencyFormat.format(0.0)}"
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate item selection
        if (selectedItem == null) {
            binding.tilItem.error = "Selecione um item"
            isValid = false
        } else {
            binding.tilItem.error = null
        }

        // Validate quantity
        val quantityStr = binding.etQuantity.text.toString()
        if (quantityStr.isBlank()) {
            binding.tilQuantity.error = "Informe a quantidade"
            isValid = false
        } else {
            try {
                val quantity = quantityStr.toInt()
                if (quantity <= 0) {
                    binding.tilQuantity.error = "Quantidade deve ser maior que zero"
                    isValid = false
                } else if (selectedItem != null && quantity > selectedItem!!.quantity) {
                    binding.tilQuantity.error = "Quantidade maior que o estoque disponível"
                    isValid = false
                } else {
                    binding.tilQuantity.error = null
                }
            } catch (e: NumberFormatException) {
                binding.tilQuantity.error = "Quantidade inválida"
                isValid = false
            }
        }

        // Validate unit price
        val unitPrice = parseCurrency(binding.etUnitPrice.text.toString())
        if (unitPrice <= 0) {
            binding.tilUnitPrice.error = "Preço deve ser maior que zero"
            isValid = false
        } else {
            binding.tilUnitPrice.error = null
        }

        return isValid
    }

    private fun registerSale() {
        selectedItem?.let { item ->
            val quantity = binding.etQuantity.text.toString().toInt()
            val unitPrice = parseCurrency(binding.etUnitPrice.text.toString())
            val totalValue = quantity * unitPrice

            salesViewModel.registerSale(item.id, quantity, totalValue)
        }

        // Observe operation status
        salesViewModel.operationStatus.observe(this, Observer { status ->
            when (status) {
                is SalesViewModel.OperationStatus.Success -> {
                    showSnackbar(status.message)
                    finish()
                }
                is SalesViewModel.OperationStatus.Error -> {
                    showSnackbar(status.message)
                }
            }
        })
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