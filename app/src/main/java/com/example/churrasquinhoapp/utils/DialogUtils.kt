package com.example.churrasquinhoapp.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.EditText
import androidx.annotation.StringRes
import com.example.churrasquinhoapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*

object DialogUtils {
    /**
     * Show simple alert dialog
     */
    fun showAlert(
        context: Context,
        title: String,
        message: String,
        positiveButton: String = context.getString(R.string.ok),
        onPositiveClick: (() -> Unit)? = null
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { dialog, _ ->
                dialog.dismiss()
                onPositiveClick?.invoke()
            }
            .show()
    }

    /**
     * Show confirmation dialog
     */
    fun showConfirmation(
        context: Context,
        title: String,
        message: String,
        positiveButton: String = context.getString(R.string.confirm),
        negativeButton: String = context.getString(R.string.cancel),
        onPositiveClick: () -> Unit,
        onNegativeClick: (() -> Unit)? = null
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButton) { dialog, _ ->
                dialog.dismiss()
                onPositiveClick()
            }
            .setNegativeButton(negativeButton) { dialog, _ ->
                dialog.dismiss()
                onNegativeClick?.invoke()
            }
            .show()
    }

    /**
     * Show error dialog
     */
    fun showError(
        context: Context,
        message: String,
        title: String = context.getString(R.string.error_occurred),
        onDismiss: (() -> Unit)? = null
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                onDismiss?.invoke()
            }
            .show()
    }

    /**
     * Show date picker dialog
     */
    fun showDatePicker(
        context: Context,
        initialDate: Calendar = Calendar.getInstance(),
        minDate: Long? = null,
        maxDate: Long? = null,
        onDateSelected: (Calendar) -> Unit
    ) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                onDateSelected(selectedDate)
            },
            initialDate.get(Calendar.YEAR),
            initialDate.get(Calendar.MONTH),
            initialDate.get(Calendar.DAY_OF_MONTH)
        ).apply {
            minDate?.let { datePicker.minDate = it }
            maxDate?.let { datePicker.maxDate = it }
        }.show()
    }

    /**
     * Show time picker dialog
     */
    fun showTimePicker(
        context: Context,
        initialTime: Calendar = Calendar.getInstance(),
        is24HourView: Boolean = true,
        onTimeSelected: (Int, Int) -> Unit
    ) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                onTimeSelected(hourOfDay, minute)
            },
            initialTime.get(Calendar.HOUR_OF_DAY),
            initialTime.get(Calendar.MINUTE),
            is24HourView
        ).show()
    }

    /**
     * Show input dialog
     */
    fun showInputDialog(
        context: Context,
        title: String,
        hint: String? = null,
        initialValue: String = "",
        onInput: (String) -> Unit
    ) {
        val input = EditText(context).apply {
            setText(initialValue)
            hint?.let { this.hint = it }
        }

        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(input)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                onInput(input.text.toString())
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    /**
     * Show list dialog
     */
    fun <T> showListDialog(
        context: Context,
        title: String,
        items: List<T>,
        itemToString: (T) -> String = { it.toString() },
        onItemSelected: (T) -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setItems(items.map(itemToString).toTypedArray()) { dialog, which ->
                dialog.dismiss()
                onItemSelected(items[which])
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    /**
     * Show single choice dialog
     */
    fun <T> showSingleChoiceDialog(
        context: Context,
        title: String,
        items: List<T>,
        selectedItem: T? = null,
        itemToString: (T) -> String = { it.toString() },
        onItemSelected: (T) -> Unit
    ) {
        val selectedIndex = selectedItem?.let { items.indexOf(it) } ?: -1

        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setSingleChoiceItems(
                items.map(itemToString).toTypedArray(),
                selectedIndex
            ) { dialog, which ->
                dialog.dismiss()
                onItemSelected(items[which])
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    /**
     * Show multiple choice dialog
     */
    fun <T> showMultiChoiceDialog(
        context: Context,
        title: String,
        items: List<T>,
        selectedItems: List<T> = emptyList(),
        itemToString: (T) -> String = { it.toString() },
        onItemsSelected: (List<T>) -> Unit
    ) {
        val checkedItems = BooleanArray(items.size) { items[it] in selectedItems }
        val selectedIndices = mutableListOf<Int>()

        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMultiChoiceItems(
                items.map(itemToString).toTypedArray(),
                checkedItems
            ) { _, which, isChecked ->
                if (isChecked) {
                    selectedIndices.add(which)
                } else {
                    selectedIndices.remove(which)
                }
            }
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                onItemsSelected(selectedIndices.map { items[it] })
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    /**
     * Show loading dialog
     */
    fun showLoading(
        context: Context,
        message: String = context.getString(R.string.loading)
    ): DialogInterface {
        return MaterialAlertDialogBuilder(context)
            .setMessage(message)
            .setCancelable(false)
            .show()
    }
}