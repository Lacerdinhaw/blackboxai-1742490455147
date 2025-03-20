package com.example.churrasquinhoapp.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object UiUtils {
    /**
     * Dialog Builders
     */
    fun showConfirmationDialog(
        context: Context,
        title: String,
        message: String,
        positiveButtonText: String = "Confirmar",
        negativeButtonText: String = "Cancelar",
        onConfirm: () -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonText) { _, _ -> onConfirm() }
            .setNegativeButton(negativeButtonText) { dialog, _ ->
                dialog.dismiss()
                onCancel?.invoke()
            }
            .show()
    }

    fun showErrorDialog(
        context: Context,
        title: String = "Erro",
        message: String,
        buttonText: String = "OK",
        onDismiss: (() -> Unit)? = null
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonText) { dialog, _ ->
                dialog.dismiss()
                onDismiss?.invoke()
            }
            .show()
    }

    /**
     * Resource Helpers
     */
    fun getColor(context: Context, @ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(context, colorRes)
    }

    fun getDrawable(context: Context, @DrawableRes drawableRes: Int): Drawable? {
        return ContextCompat.getDrawable(context, drawableRes)
    }

    fun getAnimation(context: Context, @AnimRes animRes: Int): Animation {
        return AnimationUtils.loadAnimation(context, animRes)
    }

    /**
     * Dimension Conversions
     */
    fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun spToPx(context: Context, sp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun pxToDp(context: Context, px: Int): Float {
        return px / context.resources.displayMetrics.density
    }

    /**
     * Theme Attribute Helpers
     */
    fun getThemeColor(context: Context, attrRes: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue.data
    }

    fun getThemeDimension(context: Context, attrRes: Int): Float {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue.getDimension(context.resources.displayMetrics)
    }

    /**
     * Status Bar Helpers
     */
    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier(
            "status_bar_height", "dimen", "android"
        )
        return if (resourceId > 0) {
            context.resources.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

    /**
     * Screen Metrics
     */
    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    /**
     * Input Validation Helpers
     */
    fun isValidQuantity(value: String): Boolean {
        return try {
            val quantity = value.toInt()
            quantity > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun isValidPrice(value: String): Boolean {
        return try {
            val price = value.replace(Regex("[R$,.]"), "").toDouble() / 100
            price > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    /**
     * Error Message Formatting
     */
    fun formatValidationErrors(errors: List<ValidationUtils.ValidationError>): String {
        return errors.joinToString("\n") { error ->
            "• ${error.getErrorMessage()}"
        }
    }

    /**
     * Loading State Helpers
     */
    fun handleLoadingState(
        context: Context,
        isLoading: Boolean,
        message: String = "Carregando...",
        onLoadingStarted: () -> Unit = {},
        onLoadingFinished: () -> Unit = {}
    ) {
        if (isLoading) {
            MaterialAlertDialogBuilder(context)
                .setMessage(message)
                .setCancelable(false)
                .show()
            onLoadingStarted()
        } else {
            onLoadingFinished()
        }
    }
}