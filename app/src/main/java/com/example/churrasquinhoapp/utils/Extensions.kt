package com.example.churrasquinhoapp.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import java.util.*

/**
 * Activity Extensions
 */
fun <T : Activity> Activity.startActivityWithFinish(targetActivity: Class<T>) {
    startActivity(Intent(this, targetActivity))
    finish()
}

fun Activity.hideKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.let {
        imm.hideSoftInputFromWindow(it.windowToken, 0)
    }
}

/**
 * Fragment Extensions
 */
fun Fragment.hideKeyboard() {
    activity?.hideKeyboard()
}

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), message, duration).show()
}

/**
 * Context Extensions
 */
fun Context.showLongToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}

fun Context.showShortToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

/**
 * View Extensions
 */
fun View.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, message, duration).show()
}

fun View.showSnackbarWithAction(
    message: String,
    actionText: String,
    duration: Int = Snackbar.LENGTH_LONG,
    action: () -> Unit
) {
    Snackbar.make(this, message, duration)
        .setAction(actionText) { action() }
        .show()
}

/**
 * EditText Extensions
 */
fun EditText.formatAsCurrency() {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    val cleanString = text.toString().replace(Regex("[R$,.]"), "")
    val parsed = cleanString.toDoubleOrNull()?.div(100) ?: 0.0
    setText(currencyFormat.format(parsed))
    setSelection(length())
}

fun EditText.getDoubleValue(): Double {
    val cleanString = text.toString().replace(Regex("[R$,.]"), "")
    return cleanString.toDoubleOrNull()?.div(100) ?: 0.0
}

/**
 * TextInputLayout Extensions
 */
fun TextInputLayout.clearError() {
    error = null
    isErrorEnabled = false
}

fun TextInputLayout.setErrorMessage(message: String?) {
    error = message
    isErrorEnabled = message != null
}

/**
 * LiveData Extensions
 */
fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

/**
 * ResultState Extensions
 */
fun <T> ResultState<T>.onSuccessOrError(
    onSuccess: (T) -> Unit,
    onError: (String) -> Unit
) {
    when (this) {
        is ResultState.Success -> onSuccess(data)
        is ResultState.Error -> onError(message)
        else -> {}
    }
}

/**
 * Intent Extensions
 */
inline fun <reified T : Activity> Context.startActivity(vararg params: Pair<String, Any?>) {
    val intent = Intent(this, T::class.java)
    params.forEach { (key, value) ->
        when (value) {
            is Int -> intent.putExtra(key, value)
            is Long -> intent.putExtra(key, value)
            is String -> intent.putExtra(key, value)
            is Boolean -> intent.putExtra(key, value)
            is Bundle -> intent.putExtra(key, value)
            is Parcelable -> intent.putExtra(key, value)
            else -> throw IllegalArgumentException("Type ${value?.javaClass} is not supported")
        }
    }
    startActivity(intent)
}

/**
 * Number Extensions
 */
fun Double.formatAsCurrency(): String {
    return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(this)
}

fun Int.formatAsQuantity(unit: String): String {
    return "$this $unit"
}

/**
 * String Extensions
 */
fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        }
    }
}

/**
 * AppCompatActivity Extensions
 */
fun AppCompatActivity.replaceFragment(
    @IdRes containerId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = true,
    tag: String? = null
) {
    supportFragmentManager.beginTransaction().apply {
        replace(containerId, fragment, tag)
        if (addToBackStack) {
            addToBackStack(tag)
        }
        commit()
    }
}