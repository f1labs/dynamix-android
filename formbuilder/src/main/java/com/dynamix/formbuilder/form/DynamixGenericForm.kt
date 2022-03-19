package com.dynamix.formbuilder.form

import android.content.Context
import android.content.Intent
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.dynamix.core.R
import com.dynamix.core.logger.AppLoggerProvider
import com.dynamix.core.navigation.NavigationProvider
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.google.gson.JsonObject
import java.io.File

open class DynamixGenericForm {

    private lateinit var ctx: Context
    private lateinit var formDataProvider: DynamixFormDataProvider
    lateinit var formGenerator: DynamixFormGenerator

    fun init(
        ctx: Context,
        formCode: String,
        enableAutoFocus: Boolean = true,
        formDataProvider: DynamixFormDataProvider,
        navigationProvider: NavigationProvider,
        appLoggerProvider: AppLoggerProvider,
        font: Int = R.font.regular
    ): DynamixGenericForm {
        this.ctx = ctx
        this.formDataProvider = formDataProvider

        formGenerator = DynamixFormGenerator(
            ctx,
            formCode,
            font,
            formDataProvider,
            navigationProvider,
            appLoggerProvider,
            mutableMapOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            mutableListOf(),
            enableAutoFocus
        )
        return this
    }

    fun registerViews(
        prefixMainContainer: ViewGroup,
        mainContainer: ViewGroup,
        suffixMainContainer: ViewGroup
    ): DynamixGenericForm {
        formGenerator.registerViews(prefixMainContainer, mainContainer, suffixMainContainer)
        return this
    }

    fun fieldDataHolder(): DynamixFieldDataHolder {
        return formGenerator.fieldDataHolder()
    }

    // We need to register fields before form fields processed
    fun registerFields(fieldMap: List<DynamixFormField>): DynamixGenericForm {
        formGenerator.registerFields(fieldMap)
        return this
    }

    fun processFormBuild() {
        formGenerator.processFormBuild()
    }

    fun setupFieldListeners() {
        formGenerator.setupFieldListeners()
    }

    fun getFiles(tag: String?): List<File> {
        return formGenerator.getFiles(tag)
    }

    fun getChipTexts(): Map<String, String> {
        return formGenerator.chipTexts
    }

    fun getFileMap(): HashMap<String, MutableList<File>> {
        return formGenerator.fileMap
    }

    fun selectImage() {
        formGenerator.selectImage()
    }

    fun parseImage(intent: Intent) {
        formGenerator.parseImage(intent)
    }

    fun fetchContacts(
        requestCode: Int,
        grantResults: IntArray
    ) {
        formGenerator.fetchContacts(requestCode, grantResults)
    }

    fun onImageTap(field: DynamixFormField, imageHolderWrap: LinearLayout, clearImage: ImageView) {
        formGenerator.onImageTap(field, imageHolderWrap, clearImage)
    }

    fun requestImageSelection(field: DynamixFormField) {
        formGenerator.requestImageSelection(field)
    }

    fun setupOptionsChangedListener() {
        formGenerator.setupOptionsChangedListener()
    }

    fun validateFields() {
        formGenerator.validateFields()
    }

    fun makeRequestParameters() {
        formGenerator.makeRequestParameters()
    }

    fun authenticate() {
        formDataProvider.dynamixAuthenticate()
    }

    fun authenticated(txnPassword: String, txnPasswordStatusCode: String = "") {
        formGenerator.authenticated(txnPassword, txnPasswordStatusCode)
    }

    fun onDataSelectedFromSpinner(tag: String?, value: String?) {
        formGenerator.onDataSelectedFromSpinner(tag, value)
    }

    fun onDestroy() {
        formGenerator.onDestroy()
    }
}