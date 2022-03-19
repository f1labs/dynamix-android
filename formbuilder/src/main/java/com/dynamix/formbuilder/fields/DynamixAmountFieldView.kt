package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.dynamix.formbuilder.R
import com.dynamix.core.init.DynamixEnvironmentData
import com.dynamix.core.utils.DynamixConverter
import com.dynamix.formbuilder.utils.DynamixFormHelper
import com.dynamix.formbuilder.view.DynamixAmountCardListView
import com.dynamix.core.view.DynamixNoChangingBackgroundTextInputLayout
import com.dynamix.formbuilder.data.*
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 16/12/2021.
 */
class DynamixAmountFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

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

    private fun setupAmountCardLayout(
        formField: DynamixFormField,
        inputLayout: TextInputLayout
    ): View {
        val items = ArrayList<DynamixKeyValue>()
        for ((key, value) in formField.options!!) {
            items.add(DynamixKeyValue(key ?: "", value))
        }
        return DynamixAmountCardListView(ctx, items) {
            var oldAmount = 0f
            var incrementAmount = 0f
            try {
                val oldAmountString = inputLayout.editText!!.text.toString()
                incrementAmount = it.value.toFloat()
                oldAmount = oldAmountString.toFloat()
            } catch (e: Exception) {
                // catch any exceptions
                e.printStackTrace()
            }

            // increment the amount
            val newAmount = oldAmount + incrementAmount
            inputLayout.editText!!.setText(newAmount.toString())
        }.listView
    }

    override fun render(field: DynamixFormField): View {
        val rootLayout = ConstraintLayout(ctx)
        val constraintSet = ConstraintSet()

        val amount = DynamixFormField().apply {
            fieldType = DynamixFieldType.NUMBER.fieldType
            label = field.label
            minValue = field.minValue
            maxValue = field.maxValue
            maxLength = field.maxLength
            pattern = field.pattern
            tag = field.tag
            isRequired = field.isRequired
        }
        if (field.defaultValue != null && !field.defaultValue.equals(
                "",
                ignoreCase = true
            )
        ) {
            amount.defaultValue = field.defaultValue
        }
        if (field.maxDecimalDigits != null) {
            amount.maxDecimalDigits = field.maxDecimalDigits
        }
        val amountField = DynamixTextInputFieldView().init(ctx, fieldRenderer)
            .render(field) as DynamixNoChangingBackgroundTextInputLayout
        amountField.id = R.id.dynamix_amount_field
        val layout = if (DynamixEnvironmentData.isEnableTxnLimit) {
            DynamixFormHelper.addIconToEnd(amountField, icon = R.drawable.dynamix_ic_info) {
                fieldRenderer.formDataProvider.dynamixTxnLimit(fieldRenderer.formCode)
                fieldRenderer.formDataProvider.dynamixTxnLimit(fieldRenderer.formCode, field.tag)
            }
        } else {
            amountField
        }
        layout.id = View.generateViewId()

        val amountParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        rootLayout.addView(layout, amountParams)

        // setup constraints
        constraintSet.clone(rootLayout)
        constraintSet.connect(
            layout.id,
            ConstraintSet.TOP,
            ConstraintSet.PARENT_ID,
            ConstraintSet.TOP
        )
        constraintSet.connect(
            layout.id,
            ConstraintSet.START,
            ConstraintSet.PARENT_ID,
            ConstraintSet.START
        )
        constraintSet.connect(
            layout.id,
            ConstraintSet.END,
            ConstraintSet.PARENT_ID,
            ConstraintSet.END
        )
        if (DynamixEnvironmentData.isFormAmountCardEnabled && field.options != null && field.options!!.isNotEmpty()) {
            // apply already created constraints before adding another view
            constraintSet.applyTo(rootLayout)
            val amountCardList = setupAmountCardLayout(field, amountField)
            amountCardList.id = View.generateViewId()
            val cardListParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            rootLayout.addView(amountCardList, cardListParams)
            // re clone the constraints
            constraintSet.clone(rootLayout)
            constraintSet.connect(
                amountCardList.id,
                ConstraintSet.TOP,
                layout.id,
                ConstraintSet.BOTTOM,
                DynamixConverter.dpToPx(ctx, 16)
            )
            constraintSet.connect(
                amountCardList.id,
                ConstraintSet.START,
                ConstraintSet.PARENT_ID,
                ConstraintSet.START
            )
            constraintSet.connect(
                amountCardList.id,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END
            )
            constraintSet.connect(
                amountCardList.id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )
        } else {
            // bottom constraint for amount field
            constraintSet.connect(
                layout.id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )
        }
        constraintSet.applyTo(rootLayout)
        val formFieldView = DynamixFormFieldView()
        formFieldView.formField = field
        formFieldView.view = rootLayout

        //mFormFieldViewMap.put(formField.getTag(), formFieldView);
        fieldRenderer.formFieldList.add(formFieldView)
        return rootLayout
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