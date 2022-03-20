package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.dynamix.core.extensions.dp
import com.dynamix.core.utils.DynamixResourceUtils
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
class DynamixDividerFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(formView = render(field))
    }

    override fun render(field: DynamixFormField): View {
        val divider = View(ctx)
        divider.background =
            DynamixResourceUtils.getDrawableFromThemeAttributes(
                ctx,
                R.attr.borderColor200
            )
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            1.dp(ctx),
        ).apply {
            bottomMargin = 24.dp(ctx)
        }
        divider.layoutParams = params
        val formFieldView = DynamixFormFieldView()
        formFieldView.formField = field
        formFieldView.view = divider
        fieldRenderer.formFieldList.add(formFieldView)
        return divider
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