package com.example.churrasquinhoapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.churrasquinhoapp.model.Sale
import java.util.Date

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY saleDate DESC")
    fun getAllSales(): LiveData<List<Sale>>

    @Query("SELECT * FROM sales WHERE saleDate BETWEEN :startDate AND :endDate")
    fun getSalesByDateRange(startDate: Date, endDate: Date): LiveData<List<Sale>>

    @Query("SELECT SUM(totalValue) FROM sales WHERE saleDate BETWEEN :startDate AND :endDate")
    suspend fun getTotalSalesInRange(startDate: Date, endDate: Date): Double?

    @Query("SELECT * FROM sales WHERE itemId = :itemId")
    fun getSalesByItem(itemId: Long): LiveData<List<Sale>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale): Long

    @Update
    suspend fun updateSale(sale: Sale)

    @Delete
    suspend fun deleteSale(sale: Sale)

    @Query("SELECT COUNT(*) FROM sales WHERE saleDate BETWEEN :startDate AND :endDate")
    suspend fun getSalesCountInRange(startDate: Date, endDate: Date): Int

    @Query("SELECT SUM(quantity) FROM sales WHERE itemId = :itemId")
    suspend fun getTotalQuantitySoldByItem(itemId: Long): Int?
}