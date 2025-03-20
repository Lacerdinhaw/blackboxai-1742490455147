package com.example.churrasquinhoapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.churrasquinhoapp.ui.activities.*

object NavigationUtils {
    /**
     * Activity Navigation
     */
    fun navigateToDashboard(context: Context) {
        context.startActivity(Intent(context, DashboardActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }

    fun navigateToInventory(context: Context) {
        context.startActivity(Intent(context, InventoryActivity::class.java))
    }

    fun navigateToAddEditItem(context: Context, itemId: Long? = null) {
        context.startActivity(Intent(context, AddEditItemActivity::class.java).apply {
            itemId?.let { putExtra(Constants.EXTRA_ITEM_ID, it) }
        })
    }

    fun navigateToSales(context: Context) {
        context.startActivity(Intent(context, SalesActivity::class.java))
    }

    fun navigateToAddSale(context: Context) {
        context.startActivity(Intent(context, AddSaleActivity::class.java))
    }

    /**
     * Activity Navigation with Result
     */
    fun navigateToAddEditItemForResult(
        activity: Activity,
        launcher: ActivityResultLauncher<Intent>,
        itemId: Long? = null
    ) {
        val intent = Intent(activity, AddEditItemActivity::class.java).apply {
            itemId?.let { putExtra(Constants.EXTRA_ITEM_ID, it) }
        }
        launcher.launch(intent)
    }

    fun navigateToAddSaleForResult(
        activity: Activity,
        launcher: ActivityResultLauncher<Intent>
    ) {
        val intent = Intent(activity, AddSaleActivity::class.java)
        launcher.launch(intent)
    }

    /**
     * Fragment Navigation
     */
    fun AppCompatActivity.replaceFragment(
        containerId: Int,
        fragment: Fragment,
        addToBackStack: Boolean = true,
        tag: String? = null
    ) {
        supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(containerId, fragment, tag)
            if (addToBackStack) {
                addToBackStack(tag)
            }
            commit()
        }
    }

    fun AppCompatActivity.addFragment(
        containerId: Int,
        fragment: Fragment,
        addToBackStack: Boolean = true,
        tag: String? = null
    ) {
        supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            add(containerId, fragment, tag)
            if (addToBackStack) {
                addToBackStack(tag)
            }
            commit()
        }
    }

    /**
     * Fragment Manager Extensions
     */
    fun FragmentManager.clearBackStack() {
        if (backStackEntryCount > 0) {
            val entry = getBackStackEntryAt(0)
            popBackStack(entry.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    fun FragmentManager.navigateBack(): Boolean {
        return if (backStackEntryCount > 0) {
            popBackStack()
            true
        } else {
            false
        }
    }

    /**
     * Intent Builder Extensions
     */
    inline fun <reified T : Activity> Context.buildIntent(
        vararg params: Pair<String, Any?>
    ): Intent {
        return Intent(this, T::class.java).apply {
            params.forEach { (key, value) ->
                when (value) {
                    is Int -> putExtra(key, value)
                    is Long -> putExtra(key, value)
                    is String -> putExtra(key, value)
                    is Boolean -> putExtra(key, value)
                    is Float -> putExtra(key, value)
                    is Double -> putExtra(key, value)
                    is Bundle -> putExtra(key, value)
                    is Parcelable -> putExtra(key, value)
                    is Array<*> -> when {
                        value.isArrayOf<String>() -> putExtra(key, value as Array<String>)
                        value.isArrayOf<Parcelable>() -> putExtra(key, value as Array<Parcelable>)
                    }
                    null -> putExtra(key, null as Bundle?)
                }
            }
        }
    }

    /**
     * Activity Result Helper
     */
    fun createActivityResult(data: Intent?, block: Intent.() -> Unit) {
        data?.let(block)
    }

    /**
     * Navigation Result
     */
    sealed class NavigationResult {
        object Success : NavigationResult()
        data class Error(val message: String) : NavigationResult()
    }

    /**
     * Safe Navigation
     */
    fun safeNavigate(navigation: () -> Unit): NavigationResult {
        return try {
            navigation()
            NavigationResult.Success
        } catch (e: Exception) {
            Logger.e("Navigation error", e)
            NavigationResult.Error(e.message ?: "Navigation failed")
        }
    }
}