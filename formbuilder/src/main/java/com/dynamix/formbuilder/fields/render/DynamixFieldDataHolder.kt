package com.dynamix.formbuilder.fields.render

import android.content.Context
import android.view.View
import com.dynamix.core.logger.AppLoggerProvider
import com.dynamix.core.navigation.NavigationProvider
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.form.DynamixFormDataProvider
import com.dynamix.formbuilder.image.DynamixAttachment
import java.io.File

data class DynamixFieldDataHolder(
    val ctx: Context,
    val appLoggerProvider: AppLoggerProvider,
    val formDataProvider: DynamixFormDataProvider,
    val navigationProvider: NavigationProvider,
    val formFieldList: MutableList<DynamixFormFieldView>,
    val formLabelList: MutableList<DynamixFormFieldView>,
    val formFieldViewMap: MutableMap<String, DynamixFormFieldView>,
    val contactFetcher: com.dynamix.formbuilder.contact.DynamixContactFetcher,
    val viewsNotInFormFieldList: MutableList<View>,
    val formCode: String,
    val formFieldOptionCollectionList: MutableList<DynamixFormFieldView>,
    val formFieldOptionList: MutableList<DynamixFormFieldView>,
    val font: Int,
    val fileMap: HashMap<String, MutableList<File>>,
    val attachmentMap: MutableMap<String, MutableList<DynamixAttachment>>,
    val chipTexts: MutableMap<String, String>,

    val optionsType: String = "",
)