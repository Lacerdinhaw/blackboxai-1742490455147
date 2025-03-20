package com.example.churrasquinhoapp.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import java.text.NumberFormat
import java.util.*

// View Visibility Extensions
fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

// Keyboard Extensions
fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

// Snackbar Extensions
fun View.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    action: String? = null,
    actionClick: (() -> Unit)? = null
) {
    val snackbar = Snackbar.make(this, message, duration)
    if (action != null && actionClick != null) {
        snackbar.setAction(action) { actionClick() }
    }
    snackbar.show()
}

// Toast Extensions
fun Context.showToast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(this, message, duration).show()
}

// EditText Extensions
fun EditText.setCursorToEnd() {
    setSelection(text.length)
}

// Currency Formatting Extensions
fun EditText.formatAsCurrency() {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    
    setText(currencyFormat.format(text.toString().toDoubleOrNull() ?: 0.0))
    setCursorToEnd()
}

// TextInputLayout Validation Extensions
fun TextInputLayout.validateNotEmpty(): Boolean {
    return if (editText?.text.isNullOrBlank()) {
        error = "Campo obrigatório"
        false
    } else {
        error = null
        true
    }
}

fun TextInputLayout.validateMinValue(minValue: Double): Boolean {
    val value = editText?.text.toString().toDoubleOrNull() ?: 0.0
    return if (value < minValue) {
        error = "Valor deve ser maior que ${FormatUtils.formatCurrency(minValue)}"
        false
    } else {
        error = null
        true
    }
}

fun TextInputLayout.validatePositive(): Boolean {
    val value = editText?.text.toString().toDoubleOrNull() ?: 0.0
    return if (value <= 0) {
        error = "Valor deve ser maior que zero"
        false
    } else {
        error = null
        true
    }
}

// Animation Extensions
fun View.fadeIn(duration: Long = 300) {
    alpha = 0f
    visibility = View.VISIBLE
    animate()
        .alpha(1f)
        .setDuration(duration)
        .start()
}

fun View.fadeOut(duration: Long = 300) {
    animate()
        .alpha(0f)
        .setDuration(duration)
        .withEndAction { visibility = View.GONE }
        .start()
}

// Dimension Conversions
fun Context.dpToPx(dp: Float): Float {
    return dp * resources.displayMetrics.density
}

fun Context.pxToDp(px: Float): Float {
    return px / resources.displayMetrics.density
}

// Safe Click Listener (prevents double clicks)
fun View.setOnSafeClickListener(
    debounceTime: Long = 600L,
    action: () -> Unit
) {
    var lastClickTime = 0L
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > debounceTime) {
            lastClickTime = currentTime
            action()
        }
    }
}