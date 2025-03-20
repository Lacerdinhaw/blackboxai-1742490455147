package com.example.churrasquinhoapp.utils

import androidx.room.TypeConverter
import androidx.sqlite.db.SimpleSQLiteQuery
import java.util.*

object DatabaseUtils {
    /**
     * Creates a SQL LIKE query with wildcards for searching
     */
    fun createLikeQuery(searchTerm: String): String {
        return "%${searchTerm.trim().replace(' ', '%')}%"
    }

    /**
     * Creates a SimpleSQLiteQuery for dynamic ORDER BY clauses
     */
    fun createOrderByQuery(
        baseQuery: String,
        orderByColumn: String,
        isAscending: Boolean = true
    ): SimpleSQLiteQuery {
        val direction = if (isAscending) "ASC" else "DESC"
        return SimpleSQLiteQuery("$baseQuery ORDER BY $orderByColumn $direction")
    }

    /**
     * Creates a date range condition for SQL queries
     */
    fun createDateRangeCondition(
        dateColumn: String,
        startDate: Date,
        endDate: Date
    ): String {
        return "$dateColumn BETWEEN ${startDate.time} AND ${endDate.time}"
    }

    /**
     * Safely converts a string to Double, returning null if invalid
     */
    fun safeParseDouble(value: String?): Double? {
        return try {
            value?.toDouble()
        } catch (e: NumberFormatException) {
            null
        }
    }

    /**
     * Safely converts a string to Int, returning null if invalid
     */
    fun safeParseInt(value: String?): Int? {
        return try {
            value?.toInt()
        } catch (e: NumberFormatException) {
            null
        }
    }

    /**
     * Creates a batch update query for multiple items
     */
    fun createBatchUpdateQuery(
        tableName: String,
        columnName: String,
        values: List<Pair<Long, Any>>
    ): String {
        val cases = values.joinToString(" ") { (id, value) ->
            "WHEN $id THEN $value"
        }
        val ids = values.joinToString(",") { it.first.toString() }
        return "UPDATE $tableName SET $columnName = CASE id $cases END WHERE id IN ($ids)"
    }

    /**
     * Type converters for Room database
     */
    class Converters {
        @TypeConverter
        fun fromTimestamp(value: Long?): Date? {
            return value?.let { Date(it) }
        }

        @TypeConverter
        fun dateToTimestamp(date: Date?): Long? {
            return date?.time
        }
    }

    /**
     * Database error handling
     */
    sealed class DatabaseResult<out T> {
        data class Success<T>(val data: T) : DatabaseResult<T>()
        data class Error(val exception: Exception) : DatabaseResult<Nothing>()
    }

    /**
     * Extension function to safely execute database operations
     */
    suspend fun <T> safeDbCall(call: suspend () -> T): DatabaseResult<T> {
        return try {
            DatabaseResult.Success(call())
        } catch (e: Exception) {
            DatabaseResult.Error(e)
        }
    }

    /**
     * Extension function to handle database transactions
     */
    suspend fun <T> executeTransaction(
        operations: suspend () -> T
    ): DatabaseResult<T> {
        return try {
            DatabaseResult.Success(operations())
        } catch (e: Exception) {
            DatabaseResult.Error(e)
        }
    }

    /**
     * Utility function to check if a table exists
     */
    fun doesTableExist(tableName: String): SimpleSQLiteQuery {
        return SimpleSQLiteQuery(
            "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
            arrayOf(tableName)
        )
    }

    /**
     * Utility function to get table info
     */
    fun getTableInfo(tableName: String): SimpleSQLiteQuery {
        return SimpleSQLiteQuery("PRAGMA table_info($tableName)")
    }

    /**
     * Utility function to get the count of rows in a table
     */
    fun getTableRowCount(tableName: String): SimpleSQLiteQuery {
        return SimpleSQLiteQuery("SELECT COUNT(*) FROM $tableName")
    }
}