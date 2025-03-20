package com.example.churrasquinhoapp.utils

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

object KeyboardUtils {
    /**
     * Show keyboard
     */
    fun showKeyboard(view: View) {
        view.requestFocus()
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    /**
     * Hide keyboard
     */
    fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus ?: View(activity)
        hideKeyboard(view)
    }

    fun hideKeyboard(fragment: Fragment) {
        fragment.activity?.let { hideKeyboard(it) }
    }

    /**
     * Toggle keyboard
     */
    fun toggleKeyboard(context: Context) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }

    /**
     * Check if keyboard is visible
     */
    fun isKeyboardVisible(activity: Activity): Boolean {
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)
        val screenHeight = rootView.height
        val keypadHeight = screenHeight - rect.bottom
        return keypadHeight > screenHeight * 0.15
    }

    /**
     * Set up keyboard visibility listener
     */
    fun setKeyboardVisibilityListener(
        activity: Activity,
        onKeyboardVisibilityChanged: (Boolean) -> Unit
    ): ViewTreeObserver.OnGlobalLayoutListener {
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            onKeyboardVisibilityChanged(isKeyboardVisible(activity))
        }
        rootView.viewTreeObserver.addOnGlobalLayoutListener(listener)
        return listener
    }

    /**
     * Remove keyboard visibility listener
     */
    fun removeKeyboardVisibilityListener(
        activity: Activity,
        listener: ViewTreeObserver.OnGlobalLayoutListener
    ) {
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content).getChildAt(0)
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }

    /**
     * Handle keyboard visibility with WindowInsets
     */
    fun handleKeyboardInsets(view: View, onKeyboardVisibilityChanged: (Boolean) -> Unit) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val imeHeight = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom
            onKeyboardVisibilityChanged(imeVisible && imeHeight > 0)
            insets
        }
    }

    /**
     * Extension functions for EditText
     */
    fun EditText.showKeyboard() {
        requestFocus()
        showKeyboard(this)
    }

    fun EditText.hideKeyboard() {
        clearFocus()
        hideKeyboard(this)
    }

    fun EditText.moveCursorToEnd() {
        setSelection(text.length)
    }

    fun EditText.moveCursorToStart() {
        setSelection(0)
    }

    fun EditText.selectAll() {
        setSelection(0, text.length)
    }

    /**
     * Extension functions for View
     */
    fun View.showKeyboardWithDelay(delayMillis: Long = 200) {
        postDelayed({ showKeyboard(this) }, delayMillis)
    }

    fun View.hideKeyboardWithDelay(delayMillis: Long = 200) {
        postDelayed({ hideKeyboard(this) }, delayMillis)
    }

    /**
     * Extension functions for Activity
     */
    fun Activity.setUpKeyboardListener(onKeyboardVisibilityChanged: (Boolean) -> Unit) {
        setKeyboardVisibilityListener(this, onKeyboardVisibilityChanged)
    }

    /**
     * Keyboard animations
     */
    fun animateWithKeyboard(
        view: View,
        visible: Boolean,
        duration: Long = 200,
        onAnimationEnd: (() -> Unit)? = null
    ) {
        view.animate()
            .translationY(if (visible) -view.height.toFloat() else 0f)
            .setDuration(duration)
            .withEndAction { onAnimationEnd?.invoke() }
            .start()
    }

    /**
     * Keyboard adjustment modes
     */
    enum class KeyboardAdjustMode {
        PAN,
        RESIZE,
        NOTHING
    }

    /**
     * Set keyboard adjustment mode
     */
    fun setKeyboardAdjustMode(activity: Activity, mode: KeyboardAdjustMode) {
        activity.window?.setSoftInputMode(
            when (mode) {
                KeyboardAdjustMode.PAN -> android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                KeyboardAdjustMode.RESIZE -> android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                KeyboardAdjustMode.NOTHING -> android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
            }
        )
    }
}