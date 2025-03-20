package com.example.churrasquinhoapp.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

object ResourceUtils {
    /**
     * String resources
     */
    fun getString(context: Context, @StringRes resId: Int): String {
        return context.getString(resId)
    }

    fun getString(context: Context, @StringRes resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }

    fun getStringArray(context: Context, @ArrayRes resId: Int): Array<String> {
        return context.resources.getStringArray(resId)
    }

    /**
     * Color resources
     */
    fun getColor(context: Context, @ColorRes resId: Int): Int {
        return ContextCompat.getColor(context, resId)
    }

    fun getColorStateList(context: Context, @ColorRes resId: Int): ColorStateList? {
        return ContextCompat.getColorStateList(context, resId)
    }

    /**
     * Drawable resources
     */
    fun getDrawable(context: Context, @DrawableRes resId: Int): Drawable? {
        return ContextCompat.getDrawable(context, resId)
    }

    fun getDrawableWithTint(context: Context, @DrawableRes drawableRes: Int, @ColorRes colorRes: Int): Drawable? {
        return getDrawable(context, drawableRes)?.apply {
            setTint(getColor(context, colorRes))
        }
    }

    /**
     * Dimension resources
     */
    fun getDimension(context: Context, @DimenRes resId: Int): Float {
        return context.resources.getDimension(resId)
    }

    fun getDimensionPixelSize(context: Context, @DimenRes resId: Int): Int {
        return context.resources.getDimensionPixelSize(resId)
    }

    fun getDimensionPixelOffset(context: Context, @DimenRes resId: Int): Int {
        return context.resources.getDimensionPixelOffset(resId)
    }

    /**
     * Integer resources
     */
    fun getInteger(context: Context, @IntegerRes resId: Int): Int {
        return context.resources.getInteger(resId)
    }

    fun getIntArray(context: Context, @ArrayRes resId: Int): IntArray {
        return context.resources.getIntArray(resId)
    }

    /**
     * Boolean resources
     */
    fun getBoolean(context: Context, @BoolRes resId: Int): Boolean {
        return context.resources.getBoolean(resId)
    }

    /**
     * Font resources
     */
    fun getFont(context: Context, @FontRes fontId: Int): android.graphics.Typeface? {
        return ResourcesCompat.getFont(context, fontId)
    }

    /**
     * Theme attributes
     */
    fun getThemeColor(context: Context, @AttrRes attrRes: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue.data
    }

    fun getThemeDimension(context: Context, @AttrRes attrRes: Int): Float {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue.getDimension(context.resources.displayMetrics)
    }

    /**
     * Resource name utilities
     */
    fun getResourceName(context: Context, @AnyRes resId: Int): String {
        return context.resources.getResourceName(resId)
    }

    fun getResourceTypeName(context: Context, @AnyRes resId: Int): String {
        return context.resources.getResourceTypeName(resId)
    }

    fun getResourceEntryName(context: Context, @AnyRes resId: Int): String {
        return context.resources.getResourceEntryName(resId)
    }

    /**
     * Screen metrics
     */
    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    fun getScreenDensity(context: Context): Float {
        return context.resources.displayMetrics.density
    }

    /**
     * Dimension conversions
     */
    fun dpToPx(context: Context, dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun pxToDp(context: Context, px: Float): Float {
        return px / context.resources.displayMetrics.density
    }

    fun spToPx(context: Context, sp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            context.resources.displayMetrics
        ).toInt()
    }

    /**
     * Resource error handling
     */
    sealed class ResourceError : Exception() {
        data class NotFound(@AnyRes val resId: Int) : ResourceError()
        data class InvalidFormat(@AnyRes val resId: Int) : ResourceError()
        data class Unknown(override val message: String) : ResourceError()
    }

    /**
     * Safe resource getters
     */
    fun getStringOrNull(context: Context, @StringRes resId: Int): String? {
        return try {
            getString(context, resId)
        } catch (e: Resources.NotFoundException) {
            Logger.e("Resource not found: $resId")
            null
        }
    }

    fun getColorOrNull(context: Context, @ColorRes resId: Int): Int? {
        return try {
            getColor(context, resId)
        } catch (e: Resources.NotFoundException) {
            Logger.e("Resource not found: $resId")
            null
        }
    }

    fun getDrawableOrNull(context: Context, @DrawableRes resId: Int): Drawable? {
        return try {
            getDrawable(context, resId)
        } catch (e: Resources.NotFoundException) {
            Logger.e("Resource not found: $resId")
            null
        }
    }
}