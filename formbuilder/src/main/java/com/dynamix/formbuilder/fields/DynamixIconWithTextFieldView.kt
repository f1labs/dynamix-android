package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import com.dynamix.core.utils.DynamixConverter
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
class DynamixIconWithTextFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(formView = render(field))
    }

    override fun render(field: DynamixFormField): View {
        val iconLabel = AppCompatTextView(ctx)
        iconLabel.compoundDrawablePadding =
            DynamixConverter.dpToPx(ctx, 6)
        iconLabel.setCompoundDrawablesWithIntrinsicBounds(field.iconRes, 0, 0, 0)
        iconLabel.text = field.label
        iconLabel.setTextAppearance(
            ctx,
            DynamixResourceUtils.getResolvedThemeAttribute(ctx, R.attr.textAppearanceBody2)
        )
        return iconLabel
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