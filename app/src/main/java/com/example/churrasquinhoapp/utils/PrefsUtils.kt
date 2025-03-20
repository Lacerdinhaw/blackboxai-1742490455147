package com.example.churrasquinhoapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.churrasquinhoapp.utils.Constants.PREFS_NAME
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

object PrefsUtils {
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun clear() {
        prefs.edit { clear() }
    }

    // String preferences
    fun setString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    // Int preferences
    fun setInt(key: String, value: Int) {
        prefs.edit { putInt(key, value) }
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return prefs.getInt(key, defaultValue)
    }

    // Long preferences
    fun setLong(key: String, value: Long) {
        prefs.edit { putLong(key, value) }
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return prefs.getLong(key, defaultValue)
    }

    // Float preferences
    fun setFloat(key: String, value: Float) {
        prefs.edit { putFloat(key, value) }
    }

    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return prefs.getFloat(key, defaultValue)
    }

    // Boolean preferences
    fun setBoolean(key: String, value: Boolean) {
        prefs.edit { putBoolean(key, value) }
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    // Date preferences
    fun setDate(key: String, date: Date) {
        prefs.edit { putLong(key, date.time) }
    }

    fun getDate(key: String): Date? {
        val time = prefs.getLong(key, -1)
        return if (time != -1L) Date(time) else null
    }

    // Set preferences
    fun setStringSet(key: String, value: Set<String>) {
        prefs.edit { putStringSet(key, value) }
    }

    fun getStringSet(key: String): Set<String> {
        return prefs.getStringSet(key, emptySet()) ?: emptySet()
    }

    // Object preferences (using Gson)
    inline fun <reified T> setObject(key: String, value: T) {
        val json = gson.toJson(value)
        prefs.edit { putString(key, json) }
    }

    inline fun <reified T> getObject(key: String): T? {
        val json = prefs.getString(key, null)
        return if (json != null) {
            try {
                gson.fromJson(json, T::class.java)
            } catch (e: Exception) {
                Logger.e("Error parsing JSON for key: $key", e)
                null
            }
        } else null
    }

    // List preferences (using Gson)
    inline fun <reified T> setList(key: String, list: List<T>) {
        val json = gson.toJson(list)
        prefs.edit { putString(key, json) }
    }

    inline fun <reified T> getList(key: String): List<T> {
        val json = prefs.getString(key, null)
        return if (json != null) {
            try {
                val type = object : TypeToken<List<T>>() {}.type
                gson.fromJson(json, type)
            } catch (e: Exception) {
                Logger.e("Error parsing JSON list for key: $key", e)
                emptyList()
            }
        } else emptyList()
    }

    // Remove preference
    fun remove(key: String) {
        prefs.edit { remove(key) }
    }

    // Check if preference exists
    fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    // Batch operations
    fun edit(operation: SharedPreferences.Editor.() -> Unit) {
        prefs.edit { operation() }
    }

    // Preference change listener
    fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    ) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }
}