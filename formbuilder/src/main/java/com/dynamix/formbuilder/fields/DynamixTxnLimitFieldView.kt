package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.utils.DynamixFormHelper.addIconToEnd
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFieldType
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 16/12/2021.
 */
class DynamixTxnLimitFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(formView = render(field))
    }

    override fun render(field: DynamixFormField): View {
        val fieldView = DynamixFormFieldView()
        val spinnerTxnLimit = DynamixFormField().apply {
            label = field.label
            tag = field.tag
            fieldType = DynamixFieldType.SPINNER.fieldType
            isRequired = field.isRequired
            options = field.options
            if (field.defaultValue != null && !field.defaultValue.equals(
                    "",
                    ignoreCase = true
                )
            ) {
                defaultValue = field.defaultValue
            }
        }
        val spinner = DynamixDropDownFieldView().init(ctx, fieldRenderer).render(spinnerTxnLimit)!!
        spinner.id = R.id.dynamix_spinner_txn_limit
        val layout = addIconToEnd(spinner, icon = R.drawable.dynamix_ic_info) {
            fieldRenderer.formDataProvider.dynamixTxnLimit(fieldRenderer.formCode)
            fieldRenderer.formDataProvider.dynamixTxnLimit(fieldRenderer.formCode, field.tag)
        }
        fieldView.formField = field
        fieldView.view = layout
        field.tag?.let {
            fieldRenderer.formFieldViewMap[it] = fieldView
        }
        return layout
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