package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.dynamix.core.extensions.dp
import com.dynamix.core.view.DynamixNoChangingBackgroundTextInputLayout
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
class DynamixDateRangeFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(formView = render(field))
    }

    override fun render(field: DynamixFormField): View {
        val dateRangeLayout = LinearLayout(ctx)
        dateRangeLayout.orientation = LinearLayout.VERTICAL
        val dateRangeDateContainer = LinearLayout(ctx)
        dateRangeDateContainer.orientation = LinearLayout.HORIZONTAL
        if (field.dateRangeFields != null && field.dateRangeFields!!.isNotEmpty()) {
            dateRangeLayout.addView(
                DynamixLabelFieldView(
                    ctx,
                    fieldRenderer.formLabelList,
                    fieldRenderer.formFieldViewMap
                ).render(field)
            )
            field.dateRangeFields?.forEachIndexed { index, formField ->
                val dateRangeTextInputLayout = DynamixTextInputFieldView().init(ctx, fieldRenderer)
                    .render(field) as DynamixNoChangingBackgroundTextInputLayout
                dateRangeTextInputLayout.isHintEnabled = false
                dateRangeTextInputLayout.layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                ).apply {
                    topMargin = 12.dp(ctx)
                    if (index != 0) {
                        marginStart = 8.dp(ctx)
                    }
                    if (index != (field.dateRangeFields?.size ?: 0 - 1)) {
                        marginEnd = 8.dp(ctx)
                    }
                }
                dateRangeDateContainer.addView(dateRangeTextInputLayout)
            }
        }
        dateRangeLayout.addView(dateRangeDateContainer)
        return dateRangeLayout
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