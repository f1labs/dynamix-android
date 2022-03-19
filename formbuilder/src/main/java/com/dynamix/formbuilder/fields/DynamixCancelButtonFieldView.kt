package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import android.widget.Button
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.data.DynamixFormField
import com.google.android.material.button.MaterialButton

/**
 * Created by user on 20/12/2021.
 */
class DynamixCancelButtonFieldView(
    private val ctx: Context,
    private val label: String?,
    private val onCancelButtonClick: () -> Unit,
) : DynamixFieldRenderer {
    override fun render(field: DynamixFormField): View {
        val button: Button = MaterialButton(
            ctx,
            null,
            R.attr.geFmCancelButtonStyle
        )
        button.text = label ?: ctx.getString(R.string.dynamix_action_cancel)
        button.setOnClickListener { onCancelButtonClick() }
        return button
    }
}