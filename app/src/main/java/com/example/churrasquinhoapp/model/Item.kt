package com.example.churrasquinhoapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    var quantity: Int,
    val costPrice: Double,
    val sellingPrice: Double,
    val minimumStock: Int,
    val unit: String // ex: "kg", "unidade", etc.
)