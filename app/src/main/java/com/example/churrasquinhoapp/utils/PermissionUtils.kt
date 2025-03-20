package com.example.churrasquinhoapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object PermissionUtils {
    /**
     * Check if a permission is granted
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if all permissions are granted
     */
    fun arePermissionsGranted(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { isPermissionGranted(context, it) }
    }

    /**
     * Check if permission rationale should be shown
     */
    fun shouldShowRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    /**
     * Request permissions for Activity
     */
    fun requestPermissions(
        activity: Activity,
        permissions: Array<String>,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    /**
     * Handle permission result
     */
    fun handlePermissionResult(
        grantResults: IntArray,
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ) {
        if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            onGranted()
        } else {
            onDenied()
        }
    }

    /**
     * Show permission rationale dialog
     */
    fun showRationaleDialog(
        context: Context,
        title: String,
        message: String,
        positiveButton: String = "Permitir",
        negativeButton: String = "Cancelar",
        onPositiveClick: () -> Unit,
        onNegativeClick: () -> Unit = {}
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { _, _ -> onPositiveClick() }
            .setNegativeButton(negativeButton) { _, _ -> onNegativeClick() }
            .show()
    }

    /**
     * Show settings dialog when permission is permanently denied
     */
    fun showSettingsDialog(
        context: Context,
        title: String,
        message: String,
        positiveButton: String = "Configurações",
        negativeButton: String = "Cancelar",
        onPositiveClick: () -> Unit = { openSettings(context) },
        onNegativeClick: () -> Unit = {}
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { _, _ -> onPositiveClick() }
            .setNegativeButton(negativeButton) { _, _ -> onNegativeClick() }
            .show()
    }

    /**
     * Open app settings
     */
    private fun openSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    /**
     * Extension function to register permission launcher for Activity
     */
    fun AppCompatActivity.registerPermissionLauncher(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ): ActivityResultLauncher<String> {
        return registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) onGranted() else onDenied()
        }
    }

    /**
     * Extension function to register multiple permissions launcher for Activity
     */
    fun AppCompatActivity.registerMultiplePermissionsLauncher(
        onAllGranted: () -> Unit,
        onDenied: (List<String>) -> Unit
    ): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedPermissions = permissions.filterValues { !it }.keys.toList()
            if (deniedPermissions.isEmpty()) {
                onAllGranted()
            } else {
                onDenied(deniedPermissions)
            }
        }
    }

    /**
     * Extension function to register permission launcher for Fragment
     */
    fun Fragment.registerPermissionLauncher(
        onGranted: () -> Unit,
        onDenied: () -> Unit
    ): ActivityResultLauncher<String> {
        return registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) onGranted() else onDenied()
        }
    }

    /**
     * Extension function to register multiple permissions launcher for Fragment
     */
    fun Fragment.registerMultiplePermissionsLauncher(
        onAllGranted: () -> Unit,
        onDenied: (List<String>) -> Unit
    ): ActivityResultLauncher<Array<String>> {
        return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val deniedPermissions = permissions.filterValues { !it }.keys.toList()
            if (deniedPermissions.isEmpty()) {
                onAllGranted()
            } else {
                onDenied(deniedPermissions)
            }
        }
    }

    /**
     * Extension function to check and request permission
     */
    fun Activity.checkAndRequestPermission(
        permission: String,
        launcher: ActivityResultLauncher<String>,
        rationaleTitle: String,
        rationaleMessage: String
    ) {
        when {
            isPermissionGranted(this, permission) -> {
                launcher.launch(permission)
            }
            shouldShowRationale(this, permission) -> {
                showRationaleDialog(
                    this,
                    rationaleTitle,
                    rationaleMessage,
                    onPositiveClick = { launcher.launch(permission) }
                )
            }
            else -> {
                launcher.launch(permission)
            }
        }
    }

    /**
     * Extension function to check and request multiple permissions
     */
    fun Activity.checkAndRequestPermissions(
        permissions: Array<String>,
        launcher: ActivityResultLauncher<Array<String>>,
        rationaleTitle: String,
        rationaleMessage: String
    ) {
        when {
            arePermissionsGranted(this, permissions) -> {
                launcher.launch(permissions)
            }
            permissions.any { shouldShowRationale(this, it) } -> {
                showRationaleDialog(
                    this,
                    rationaleTitle,
                    rationaleMessage,
                    onPositiveClick = { launcher.launch(permissions) }
                )
            }
            else -> {
                launcher.launch(permissions)
            }
        }
    }
}