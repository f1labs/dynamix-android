package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.dynamix.core.logger.LoggerProviderUtils
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 16/12/2021.
 */
class DynamixLabelFieldView(
    private val ctx: Context,
    private val formFieldList: MutableList<DynamixFormFieldView>,
    private val formFieldViewMap: MutableMap<String, DynamixFormFieldView>,
) : DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView()
    }


    override fun render(field: DynamixFormField): View? {

        val formFieldView = DynamixFormFieldView()
        val formField1 = DynamixFormField()
        if (field.isHidden) return null
        formField1.visibilityParent = field.visibilityParent
        formField1.visibilityTabParent = field.visibilityTabParent
        formField1.visibilityValues = field.visibilityValues
        if (field.label.isNotEmpty() && !field.label.equals("", ignoreCase = true)) {
            val tvLabel = AppCompatTextView(ctx, null, R.attr.geFmLabelStyle)
            tvLabel.text = field.label
            tvLabel.tag = field.tag + "__Label"
            formFieldView.formField = formField1
            formFieldView.view = tvLabel
            formFieldList.add(formFieldView)
            LoggerProviderUtils.debug("LOGGER : " + field.label + " " + field.tag)
            field.tag?.let {
                formFieldViewMap[tvLabel.tag.toString()] = formFieldView
            }
            return tvLabel
        }
        return null;
    }

    override fun validate(formFieldView: DynamixFormFieldView): Boolean {
        return true
    }

    override fun requestParams(
        formFieldView: DynamixFormFieldView,
        requestParams: JSONObject,
        merchantRequest: JSONObject,
        merchantRequestParams: JSONArray,
        listConfirmationData: MutableList<DynamixConfirmationModel>
    ) {

    }
}