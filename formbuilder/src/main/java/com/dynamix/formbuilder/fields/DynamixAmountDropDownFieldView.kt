package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import com.dynamix.core.init.DynamixEnvironmentData
import com.dynamix.formbuilder.utils.DynamixFormHelper
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFieldType
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 16/12/2021.
 */
class DynamixAmountDropDownFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(
            labelView = DynamixLabelFieldView(
                ctx,
                fieldRenderer.formLabelList,
                fieldRenderer.formFieldViewMap
            ).render(field),
            formView = render(field)
        )
    }

    override fun render(field: DynamixFormField): View {
        val formFieldView = DynamixFormFieldView()
        val spinnerTxnLimit = DynamixFormField().apply {
            label = field.label
            tag = field.tag
            fieldType = DynamixFieldType.SPINNER.fieldType
            options = field.options
            defaultValue = field.defaultValue
        }
        val amountField = DynamixDropDownFieldView().init(ctx, fieldRenderer)
            .render(spinnerTxnLimit) as TextInputLayout
        amountField.id = R.id.dynamix_spinner_txn_limit
        val layout = if (DynamixEnvironmentData.isEnableTxnLimit) {
            DynamixFormHelper.addIconToEnd(
                amountField,
                fieldRenderer.viewsNotInFormFieldList,
                R.drawable.dynamix_ic_info
            ) {
                fieldRenderer.formDataProvider.dynamixTxnLimit(fieldRenderer.formCode)
                fieldRenderer.formDataProvider.dynamixTxnLimit(fieldRenderer.formCode, field.tag)
            }
        } else {
            amountField
        }
        formFieldView.formField = field
        formFieldView.view = layout

        //mFormFieldViewMap.put(formField.getTag(), formFieldView);
        fieldRenderer.formFieldList.add(formFieldView)
        fieldRenderer.viewsNotInFormFieldList.add(layout.getChildAt(1))
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