package com.example.churrasquinhoapp.viewmodel

import androidx.lifecycle.*
import com.example.churrasquinhoapp.model.Sale
import com.example.churrasquinhoapp.repository.InventoryRepository
import kotlinx.coroutines.launch
import java.util.*

class SalesViewModel(private val repository: InventoryRepository) : ViewModel() {
    
    val allSales: LiveData<List<Sale>> = repository.getAllSales()

    private val _operationStatus = MutableLiveData<OperationStatus>()
    val operationStatus: LiveData<OperationStatus> = _operationStatus

    private val _salesStats = MutableLiveData<InventoryRepository.SalesStats>()
    val salesStats: LiveData<InventoryRepository.SalesStats> = _salesStats

    fun registerSale(itemId: Long, quantity: Int, totalValue: Double) {
        if (validateSaleInput(quantity, totalValue)) {
            viewModelScope.launch {
                repository.registerSale(itemId, quantity, totalValue)
                    .onSuccess { 
                        _operationStatus.value = OperationStatus.Success("Venda registrada com sucesso")
                        updateSalesStats()
                    }
                    .onFailure { 
                        _operationStatus.value = OperationStatus.Error("Erro ao registrar venda: ${it.message}")
                    }
            }
        } else {
            _operationStatus.value = OperationStatus.Error("Dados inválidos. Verifique os campos.")
        }
    }

    fun updateSalesStats(startDate: Date = getStartOfDay(), endDate: Date = Date()) {
        viewModelScope.launch {
            repository.getSalesStats(startDate, endDate)
                .onSuccess { stats -> 
                    _salesStats.value = stats
                }
                .onFailure { 
                    _operationStatus.value = OperationStatus.Error("Erro ao atualizar estatísticas: ${it.message}")
                }
        }
    }

    private fun validateSaleInput(quantity: Int, totalValue: Double): Boolean {
        return quantity > 0 && totalValue > 0
    }

    private fun getStartOfDay(): Date {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    sealed class OperationStatus {
        data class Success(val message: String) : OperationStatus()
        data class Error(val message: String) : OperationStatus()
    }

    class Factory(private val repository: InventoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SalesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SalesViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}