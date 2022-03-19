package com.dynamix.formbuilder.fields

import DynamixFormFieldConstants
import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.dynamix.core.utils.DynamixAmountFormatUtil
import com.dynamix.core.utils.DynamixCommonUtils
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import com.dynamix.formbuilder.validations.DynamixFormBuilderValidationUtils
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 16/12/2021.
 */
class DynamixLabelValueFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(formView = render(field))
    }

    override fun render(field: DynamixFormField): View? {
        val linearLayout = LinearLayout(ctx)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        val tvLabel = TextView(ctx, null, R.attr.geFmLabelValueLabelStyle)
        tvLabel.id = R.id.dynamix_label_value_label
        tvLabel.text = field.label
        val tvValue = TextView(ctx, null, R.attr.geFmLabelValueValueStyle)
        tvValue.id = R.id.dynamix_label_value_value
        tvValue.text = field.displayValue
        linearLayout.addView(tvLabel)
        linearLayout.addView(tvValue)
        val fieldView = DynamixFormFieldView()
        if (field.isHidden) return null
        fieldView.formField = field
        fieldView.view = linearLayout
        fieldView.fieldHandler = this
        field.tag?.let {
            fieldRenderer.formFieldViewMap[it] = fieldView
        }
        fieldRenderer.formFieldList.add(fieldView)
        return linearLayout
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
        if (DynamixCommonUtils.isNumeric(formFieldView.formField.tag!!)) {
            merchantRequest.put(
                DynamixFormFieldConstants.PARAM_ORDER,
                formFieldView.formField.tag
            )
            merchantRequest.put(
                DynamixFormFieldConstants.LABEL,
                formFieldView.formField.label
            )
            merchantRequest.put(
                DynamixFormFieldConstants.PARAM_VALUE,
                formFieldView.formField.labelValue
            )
            merchantRequestParams.put(merchantRequest)
        } else {
            requestParams.put(
                formFieldView.formField.tag!!,
                formFieldView.formField.labelValue
            )
        }
        if (DynamixFormBuilderValidationUtils.isAmountField(formFieldView)) {
            if (formFieldView.formField.confirmationLabel != null
                && formFieldView.formField.confirmationLabel!!.isNotEmpty()
            ) {
                listConfirmationData.add(
                    DynamixConfirmationModel(
                        formFieldView.formField.confirmationLabel!!,
                        DynamixAmountFormatUtil.formatAmountWithCurrencyCode(
                            formFieldView.formField.labelValue
                        )
                    )
                )
            } else {
                listConfirmationData.add(
                    DynamixConfirmationModel(
                        formFieldView.formField.label,
                        DynamixAmountFormatUtil.formatAmountWithCurrencyCode(
                            formFieldView.formField.labelValue
                        )
                    )
                )
            }
        } else {
            if (formFieldView.formField.confirmationLabel != null
                && formFieldView.formField.confirmationLabel!!.isNotEmpty()
            ) {
                listConfirmationData.add(
                    DynamixConfirmationModel(
                        formFieldView.formField.confirmationLabel!!,
                        formFieldView.formField.labelValue!!
                    )
                )
            } else {
                listConfirmationData.add(
                    DynamixConfirmationModel(
                        formFieldView.formField.label,
                        formFieldView.formField.labelValue!!
                    )
                )
            }
        }
    }
}