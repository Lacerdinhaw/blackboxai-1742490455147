package com.example.churrasquinhoapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.churrasquinhoapp.model.Item

@Dao
interface ItemDao {
    @Query("SELECT * FROM items ORDER BY name ASC")
    fun getAllItems(): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE id = :itemId")
    suspend fun getItemById(itemId: Long): Item?

    @Query("SELECT * FROM items WHERE quantity <= minimumStock")
    fun getLowStockItems(): LiveData<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item): Long

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

    @Query("UPDATE items SET quantity = quantity - :soldQuantity WHERE id = :itemId")
    suspend fun updateStockAfterSale(itemId: Long, soldQuantity: Int)

    @Query("SELECT quantity FROM items WHERE id = :itemId")
    suspend fun getItemQuantity(itemId: Long): Int
}