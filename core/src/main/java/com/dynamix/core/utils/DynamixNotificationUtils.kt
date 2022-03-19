package com.dynamix.core.utils

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dynamix.core.R

object DynamixNotificationUtils {
    val successMessage = "Operation completed successfully"
    val failureMessage = "Something went wrong"

    fun showInfo(context: Context, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showLongInfo(context: Context, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun showErrorInfo(context: Context, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun showLongErrorInfo(context: Context, message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun successDialog(context: Context, message: String?) {
        getSuccessDialog(context, message ?: successMessage)
            .setPositiveButton(context.getString(R.string.dynamix_action_done)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    fun errorDialog(context: Context, message: String?) {
        getErrorDialog(context, message ?: failureMessage)
            .setPositiveButton(context.getString(R.string.dynamix_action_done)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun getSuccessDialog(context: Context, message: String): AlertDialog.Builder {
        return AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.dynamix_title_success))
            .setMessage(message)
            .setCancelable(false)
    }

    private fun getErrorDialog(context: Context, message: String): AlertDialog.Builder {
        return AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.dynamix_title_error))
            .setMessage(message)
            .setCancelable(false)
    }
}