package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import com.dynamix.formbuilder.data.DynamixConfirmationModel
import com.dynamix.formbuilder.data.DynamixFormField
import com.dynamix.formbuilder.data.DynamixFormFieldView
import com.dynamix.formbuilder.fields.render.DynamixFieldDataHolder
import com.dynamix.formbuilder.fields.render.DynamixFormView
import im.delight.android.webview.AdvancedWebView
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by user on 16/12/2021.
 */
class DynamixWebDataFieldView : DynamixBaseFieldView(), DynamixFormDataHandler {

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
        val advancedWebView = AdvancedWebView(ctx)
        if (field.webUrl != null && field.webUrl!!.isNotEmpty()) {
            advancedWebView.loadUrl(field.webUrl!!)
        }
        if (field.webData != null && field.webData!!.isNotEmpty()) {
            advancedWebView.loadHtml(field.webData)
        }
        root.addView(advancedWebView)
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