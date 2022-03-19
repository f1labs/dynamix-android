package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import com.dynamix.core.utils.DynamixConverter
import com.dynamix.core.utils.DynamixResourceUtils.getColor
import com.dynamix.core.utils.DynamixResourceUtils.getColorFromThemeAttributes
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
class DynamixNotesFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(formView = render(field))
    }

    override fun render(field: DynamixFormField): View {
        val root = LinearLayout(ctx)
        root.orientation = LinearLayout.VERTICAL
        val notesTitle = AppCompatTextView(ctx)
        notesTitle.setTextColor(
            getColorFromThemeAttributes(
                ctx,
                R.attr.colorOnSurface
            )
        )
        notesTitle.setTextAppearance(
            ctx,
            getResolvedThemeAttribute(ctx, R.attr.textAppearanceHeadline5)
        )
        notesTitle.text = field.label
        val titleParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val margin8dp = DynamixConverter.dpToPx(ctx, 8)
        titleParams.setMargins(0, margin8dp, 0, 0)
        root.addView(notesTitle, titleParams)

        // loop through all the notes
        for (i in field.notes!!.indices) {
            val notesDesc = AppCompatTextView(ctx)
            notesDesc.setTextAppearance(
                ctx,
                getResolvedThemeAttribute(ctx, R.attr.textAppearanceBody2)
            )
            notesDesc.setTextColor(
                getColor(
                    ctx,
                    R.color.material_on_surface_emphasis_medium
                )
            )
            ((i + 1).toString() + ". " + field.notes!![i]).also { notesDesc.text = it }
            val notesParam = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            notesParam.setMargins(margin8dp, 0, margin8dp, margin8dp)
            root.addView(notesDesc, notesParam)
        }
        return root
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