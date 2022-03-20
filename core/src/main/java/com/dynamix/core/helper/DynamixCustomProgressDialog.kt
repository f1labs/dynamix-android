package com.dynamix.core.helper

import android.app.ProgressDialog
import android.content.Context
import androidx.appcompat.view.ContextThemeWrapper
import com.dynamix.core.R

class DynamixCustomProgressDialog(context: Context) : ProgressDialog(context) {

    private val progressDialog: ProgressDialog =
        ProgressDialog(ContextThemeWrapper(context, R.style.Theme_Core_ProgressDialogTheme))

    init {
        progressDialog.setIndeterminate(true)
        progressDialog.setProgressNumberFormat(null)
        progressDialog.setProgressPercentFormat(null)
        progressDialog.setProgressStyle(STYLE_SPINNER)
    }

    fun setMessage(message: String) {
        progressDialog.setMessage(message)
    }

    override fun setCancelable(cancelable: Boolean) {
        progressDialog.setCancelable(cancelable)
    }

    override fun show() {
        if (!progressDialog.isShowing) {
            progressDialog.show()
        }
    }

    override fun dismiss() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }
}