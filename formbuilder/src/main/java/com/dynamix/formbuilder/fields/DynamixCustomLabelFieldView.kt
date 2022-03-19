package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import com.dynamix.core.utils.DynamixConverter.dpToPx
import com.dynamix.core.utils.DynamixResourceUtils.getColor
import com.dynamix.core.utils.DynamixResourceUtils.getResolvedThemeAttribute
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
class DynamixCustomLabelFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(formView = render(field))
    }

    override fun render(field: DynamixFormField): View {
        val label = AppCompatTextView(ctx)
        label.setTextAppearance(
            ctx,
            getResolvedThemeAttribute(ctx, field.textAppearance)
        )
        label.text = field.label
        if (field.textColor != null) {
            label.setTextColor(getColor(ctx, field.textColor!!))
        } else {
            label.setTextColor(
                getColor(
                    ctx,
                    R.color.material_on_surface_emphasis_high_type
                )
            )
        }
        if (field.iconRes != 0) {
            label.setCompoundDrawablesRelativeWithIntrinsicBounds(
                ResourcesCompat.getDrawable(ctx.resources, field.iconRes, ctx.theme),
                null,
                null,
                null
            )
            label.compoundDrawablePadding = dpToPx(ctx, 10f)
        }
        val formFieldView = DynamixFormFieldView()
        formFieldView.formField = field
        formFieldView.view = label
        fieldRenderer.formFieldList.add(formFieldView)
        return label
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