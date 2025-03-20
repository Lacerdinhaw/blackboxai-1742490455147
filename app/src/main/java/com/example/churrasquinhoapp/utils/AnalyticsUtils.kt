package com.example.churrasquinhoapp.utils

import android.os.Bundle
import com.example.churrasquinhoapp.BuildConfig

object AnalyticsUtils {
    private const val TAG = "Analytics"

    // Event Names
    object Events {
        // Screen Views
        const val SCREEN_VIEW = "screen_view"
        const val DASHBOARD_VIEW = "dashboard_view"
        const val INVENTORY_VIEW = "inventory_view"
        const val SALES_VIEW = "sales_view"
        const val ADD_ITEM_VIEW = "add_item_view"
        const val EDIT_ITEM_VIEW = "edit_item_view"
        const val ADD_SALE_VIEW = "add_sale_view"

        // User Actions
        const val ITEM_ADDED = "item_added"
        const val ITEM_EDITED = "item_edited"
        const val ITEM_DELETED = "item_deleted"
        const val SALE_REGISTERED = "sale_registered"
        const val INVENTORY_FILTERED = "inventory_filtered"
        const val SALES_FILTERED = "sales_filtered"
        const val LOW_STOCK_ALERT = "low_stock_alert"

        // Error Events
        const val ERROR_OCCURRED = "error_occurred"
        const val VALIDATION_ERROR = "validation_error"
        const val DATABASE_ERROR = "database_error"
    }

    // Parameter Names
    object Params {
        const val SCREEN_NAME = "screen_name"
        const val ITEM_ID = "item_id"
        const val ITEM_NAME = "item_name"
        const val ITEM_QUANTITY = "item_quantity"
        const val ITEM_PRICE = "item_price"
        const val SALE_ID = "sale_id"
        const val SALE_AMOUNT = "sale_amount"
        const val ERROR_TYPE = "error_type"
        const val ERROR_MESSAGE = "error_message"
        const val FILTER_TYPE = "filter_type"
        const val DATE_RANGE = "date_range"
    }

    /**
     * Log screen view
     */
    fun logScreenView(screenName: String) {
        if (!BuildConfig.DEBUG) {
            val params = Bundle().apply {
                putString(Params.SCREEN_NAME, screenName)
            }
            logEvent(Events.SCREEN_VIEW, params)
        }
    }

    /**
     * Log item related events
     */
    fun logItemAdded(itemId: Long, itemName: String, quantity: Int, price: Double) {
        if (!BuildConfig.DEBUG) {
            val params = Bundle().apply {
                putLong(Params.ITEM_ID, itemId)
                putString(Params.ITEM_NAME, itemName)
                putInt(Params.ITEM_QUANTITY, quantity)
                putDouble(Params.ITEM_PRICE, price)
            }
            logEvent(Events.ITEM_ADDED, params)
        }
    }

    fun logItemEdited(itemId: Long, itemName: String, quantity: Int, price: Double) {
        if (!BuildConfig.DEBUG) {
            val params = Bundle().apply {
                putLong(Params.ITEM_ID, itemId)
                putString(Params.ITEM_NAME, itemName)
                putInt(Params.ITEM_QUANTITY, quantity)
                putDouble(Params.ITEM_PRICE, price)
            }
            logEvent(Events.ITEM_EDITED, params)
        }
    }

    fun logItemDeleted(itemId: Long, itemName: String) {
        if (!BuildConfig.DEBUG) {
            val params = Bundle().apply {
                putLong(Params.ITEM_ID, itemId)
                putString(Params.ITEM_NAME, itemName)
            }
            logEvent(Events.ITEM_DELETED, params)
        }
    }

    /**
     * Log sale related events
     */
    fun logSaleRegistered(saleId: Long, amount: Double) {
        if (!BuildConfig.DEBUG) {
            val params = Bundle().apply {
                putLong(Params.SALE_ID, saleId)
                putDouble(Params.SALE_AMOUNT, amount)
            }
            logEvent(Events.SALE_REGISTERED, params)
        }
    }

    /**
     * Log filter events
     */
    fun logInventoryFiltered(filterType: String) {
        if (!BuildConfig.DEBUG) {
            val params = Bundle().apply {
                putString(Params.FILTER_TYPE, filterType)
            }
            logEvent(Events.INVENTORY_FILTERED, params)
        }
    }

    fun logSalesFiltered(dateRange: String) {
        if (!BuildConfig.DEBUG) {
            val params = Bundle().apply {
                putString(Params.DATE_RANGE, dateRange)
            }
            logEvent(Events.SALES_FILTERED, params)
        }
    }

    /**
     * Log error events
     */
    fun logError(errorType: String, errorMessage: String) {
        if (!BuildConfig.DEBUG) {
            val params = Bundle().apply {
                putString(Params.ERROR_TYPE, errorType)
                putString(Params.ERROR_MESSAGE, errorMessage)
            }
            logEvent(Events.ERROR_OCCURRED, params)
        }
    }

    fun logValidationError(errorType: String, errorMessage: String) {
        if (!BuildConfig.DEBUG) {
            val params = Bundle().apply {
                putString(Params.ERROR_TYPE, errorType)
                putString(Params.ERROR_MESSAGE, errorMessage)
            }
            logEvent(Events.VALIDATION_ERROR, params)
        }
    }

    fun logDatabaseError(errorType: String, errorMessage: String) {
        if (!BuildConfig.DEBUG) {
            val params = Bundle().apply {
                putString(Params.ERROR_TYPE, errorType)
                putString(Params.ERROR_MESSAGE, errorMessage)
            }
            logEvent(Events.DATABASE_ERROR, params)
        }
    }

    /**
     * Base log event function
     */
    private fun logEvent(eventName: String, params: Bundle? = null) {
        // Log to console in debug mode
        if (BuildConfig.DEBUG) {
            Logger.d("Analytics Event: $eventName, Params: $params", TAG)
            return
        }

        // Here you would implement your actual analytics logging
        // For example, using Firebase Analytics:
        // FirebaseAnalytics.getInstance(context).logEvent(eventName, params)
    }
}