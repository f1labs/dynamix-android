package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.dynamix.core.extensions.dp
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 20/12/2021.
 */
class DynamixTextFieldChildrenFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(formView = render(field))
    }

    override fun render(field: DynamixFormField): View {
        val layout = LinearLayout(ctx)
        layout.orientation = LinearLayout.HORIZONTAL
        for (i in field.childFields!!.indices) {
            layout.addView(getTextFieldChildren(field.childFields!![i], i != 0))
        }
        val formFieldView = DynamixFormFieldView()
        formFieldView.formField = field
        formFieldView.view = layout
        fieldRenderer.formFieldList.add(formFieldView)
        return layout
    }

    private fun getTextFieldChildren(field: DynamixFormField, applyMargin: Boolean): LinearLayout {
        val layout = LinearLayout(ctx)
        layout.orientation = LinearLayout.VERTICAL
        val layoutParams = LinearLayout.LayoutParams(
            0, ViewGroup.LayoutParams.WRAP_CONTENT, field.weight
                .toFloat()
        )
        layout.layoutParams = layoutParams
        if (applyMargin) {
            layoutParams.setMargins(8.dp(ctx), 0, 0, 0)
        }
        val label = DynamixLabelFieldView(
            ctx,
            fieldRenderer.formLabelList,
            fieldRenderer.formFieldViewMap
        ).render(field)
        val inputField = DynamixTextInputFieldView().init(ctx, fieldRenderer).render(field)
        layout.addView(label)
        layout.addView(inputField)
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