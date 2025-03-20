package com.example.churrasquinhoapp.repository

import androidx.lifecycle.LiveData
import com.example.churrasquinhoapp.dao.ItemDao
import com.example.churrasquinhoapp.dao.SaleDao
import com.example.churrasquinhoapp.model.Item
import com.example.churrasquinhoapp.model.Sale
import java.util.Date
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InventoryRepository(
    private val itemDao: ItemDao,
    private val saleDao: SaleDao
) {
    // Item operations
    val allItems: LiveData<List<Item>> = itemDao.getAllItems()
    val lowStockItems: LiveData<List<Item>> = itemDao.getLowStockItems()

    suspend fun insertItem(item: Item): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val id = itemDao.insertItem(item)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateItem(item: Item): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            itemDao.updateItem(item)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteItem(item: Item): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            itemDao.deleteItem(item)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getItemById(id: Long): Result<Item> = withContext(Dispatchers.IO) {
        try {
            val item = itemDao.getItemById(id)
            if (item != null) {
                Result.success(item)
            } else {
                Result.failure(Exception("Item not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Sale operations
    suspend fun registerSale(itemId: Long, quantity: Int, totalValue: Double): Result<Long> = 
        withContext(Dispatchers.IO) {
            try {
                val currentQuantity = itemDao.getItemQuantity(itemId)
                if (currentQuantity < quantity) {
                    return@withContext Result.failure(
                        Exception("Quantidade insuficiente em estoque")
                    )
                }

                val sale = Sale(
                    itemId = itemId,
                    quantity = quantity,
                    totalValue = totalValue
                )

                val saleId = saleDao.insertSale(sale)
                itemDao.updateStockAfterSale(itemId, quantity)
                
                Result.success(saleId)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    fun getAllSales(): LiveData<List<Sale>> = saleDao.getAllSales()

    suspend fun getSalesStats(startDate: Date, endDate: Date): Result<SalesStats> = 
        withContext(Dispatchers.IO) {
            try {
                val totalValue = saleDao.getTotalSalesInRange(startDate, endDate) ?: 0.0
                val count = saleDao.getSalesCountInRange(startDate, endDate)
                
                Result.success(SalesStats(totalValue, count))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    data class SalesStats(
        val totalValue: Double,
        val count: Int
    )
}