package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.dynamix.core.utils.DynamixConverter.dpToPx
import com.dynamix.core.utils.DynamixResourceUtils.getResolvedThemeAttribute
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import imageview.avatar.com.avatarimageview.GlideApp
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 16/12/2021.
 */
class DynamixLabelImageFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

    private val optionImageHeight = 24f
    private val optionImageScaleType = ImageView.ScaleType.CENTER_INSIDE

    override fun init(ctx: Context, fieldRenderer: DynamixFieldDataHolder): DynamixFormDataHandler {
        this.ctx = ctx
        this.fieldRenderer = fieldRenderer
        return this
    }

    override fun renderField(field: DynamixFormField): DynamixFormView {
        return DynamixFormView(formView = render(field))
    }

    override fun render(field: DynamixFormField): View {
        val parent = LinearLayout(ctx)
        parent.orientation = LinearLayout.HORIZONTAL
        parent.gravity = Gravity.CENTER
        val textView = AppCompatTextView(ctx)
        textView.gravity = Gravity.CENTER_VERTICAL
        if (field.label.isNotEmpty()) {
            textView.text = field.label
        }
        if (field.textAppearance != 0) {
            textView.setTextAppearance(
                ctx,
                getResolvedThemeAttribute(ctx, field.textAppearance)
            )
        }
        val mOptionsImage = AppCompatImageView(ctx)
        val menuImageParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            dpToPx(ctx, optionImageHeight)
        )
        menuImageParams.setMargins(0, 0, 0, 0)
        mOptionsImage.layoutParams = menuImageParams
        mOptionsImage.id = R.id.dynamix_menu_image
        mOptionsImage.scaleType = optionImageScaleType
        GlideApp.with(ctx)
            .load(field.iconUrl)
            .into(mOptionsImage)
        parent.addView(textView)
        parent.addView(mOptionsImage)
        val formFieldView = DynamixFormFieldView()
        formFieldView.formField = field
        formFieldView.view = parent
        fieldRenderer.formFieldList.add(formFieldView)
        return parent
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