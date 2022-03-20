package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import com.google.android.material.button.MaterialButton
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 16/12/2021.
 */
class DynamixButtonFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(formView = render(field))
    }

    override fun render(field: DynamixFormField): View {
        val parentContainer = LinearLayout(ctx)
        parentContainer.orientation = LinearLayout.VERTICAL
        val button = MaterialButton(
            ContextThemeWrapper(ctx, R.style.ThemeOverlay_Dynamix_Form_Button),
            null,
            R.attr.materialButtonOutlinedStyle
        )
        button.text = field.label
        if (field.iconRes != 0) {
            button.icon = ContextCompat.getDrawable(ctx, field.iconRes)
            button.iconGravity = MaterialButton.ICON_GRAVITY_START
        }
        button.setOnClickListener {
            fieldRenderer.formDataProvider.dynamixOnButtonClick(
                field,
                field.tag!!
            )
        }
        parentContainer.addView(button)
        // only add to FormFieldViewMap if we have tag
        field.tag?.let {
            val formFieldView = DynamixFormFieldView()
            formFieldView.formField = field
            formFieldView.view = parentContainer
            fieldRenderer.formFieldViewMap[it] = formFieldView
        }
        return parentContainer
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