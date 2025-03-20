package com.example.churrasquinhoapp

import android.app.Application
import com.example.churrasquinhoapp.database.AppDatabase
import com.example.churrasquinhoapp.repository.InventoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ChurrasquinhoApp : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    // Lazy database instance
    private val database by lazy {
        AppDatabase.getDatabase(this)
    }

    // Lazy repository instance
    val repository by lazy {
        InventoryRepository(database.itemDao(), database.saleDao())
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        private lateinit var instance: ChurrasquinhoApp

        fun getInstance(): ChurrasquinhoApp = instance
    }
}