package com.example.churrasquinhoapp.viewmodel

import androidx.lifecycle.*
import com.example.churrasquinhoapp.model.Item
import com.example.churrasquinhoapp.repository.InventoryRepository
import kotlinx.coroutines.launch
import java.util.*

class InventoryViewModel(private val repository: InventoryRepository) : ViewModel() {
    
    // LiveData for all items in inventory
    val allItems: LiveData<List<Item>> = repository.allItems

    // LiveData for items with low stock
    val lowStockItems: LiveData<List<Item>> = repository.lowStockItems

    private val _operationStatus = MutableLiveData<OperationStatus>()
    val operationStatus: LiveData<OperationStatus> = _operationStatus

    fun addItem(
        name: String,
        quantity: Int,
        costPrice: Double,
        sellingPrice: Double,
        minimumStock: Int,
        unit: String
    ) {
        if (validateItemInput(name, quantity, costPrice, sellingPrice, minimumStock)) {
            viewModelScope.launch {
                val item = Item(
                    name = name,
                    quantity = quantity,
                    costPrice = costPrice,
                    sellingPrice = sellingPrice,
                    minimumStock = minimumStock,
                    unit = unit
                )
                repository.insertItem(item)
                    .onSuccess { 
                        _operationStatus.value = OperationStatus.Success("Item adicionado com sucesso")
                    }
                    .onFailure { 
                        _operationStatus.value = OperationStatus.Error("Erro ao adicionar item: ${it.message}")
                    }
            }
        } else {
            _operationStatus.value = OperationStatus.Error("Dados inválidos. Verifique os campos.")
        }
    }

    fun updateItem(item: Item) {
        if (validateItemInput(item.name, item.quantity, item.costPrice, item.sellingPrice, item.minimumStock)) {
            viewModelScope.launch {
                repository.updateItem(item)
                    .onSuccess { 
                        _operationStatus.value = OperationStatus.Success("Item atualizado com sucesso")
                    }
                    .onFailure { 
                        _operationStatus.value = OperationStatus.Error("Erro ao atualizar item: ${it.message}")
                    }
            }
        } else {
            _operationStatus.value = OperationStatus.Error("Dados inválidos. Verifique os campos.")
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.deleteItem(item)
                .onSuccess { 
                    _operationStatus.value = OperationStatus.Success("Item removido com sucesso")
                }
                .onFailure { 
                    _operationStatus.value = OperationStatus.Error("Erro ao remover item: ${it.message}")
                }
        }
    }

    fun getItemById(id: Long): LiveData<Result<Item>> {
        val result = MutableLiveData<Result<Item>>()
        viewModelScope.launch {
            result.value = repository.getItemById(id)
        }
        return result
    }

    private fun validateItemInput(
        name: String,
        quantity: Int,
        costPrice: Double,
        sellingPrice: Double,
        minimumStock: Int
    ): Boolean {
        return name.isNotBlank() &&
                quantity >= 0 &&
                costPrice > 0 &&
                sellingPrice > 0 &&
                minimumStock >= 0 &&
                sellingPrice > costPrice
    }

    sealed class OperationStatus {
        data class Success(val message: String) : OperationStatus()
        data class Error(val message: String) : OperationStatus()
    }

    class Factory(private val repository: InventoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return InventoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}