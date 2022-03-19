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
class DynamixSubmitButtonFieldView(
    private val ctx: Context,
    private val submitButton: String?,
    private val onSubmitButtonClick: () -> Unit,
) : DynamixFieldRenderer {
    override fun render(field: DynamixFormField): View {
        val button: Button = MaterialButton(
            ctx,
            null,
            R.attr.geFmSubmitButtonStyle,
        )
        button.text = submitButton ?: ctx.getString(R.string.dynamix_action_submit)
        button.setOnClickListener { onSubmitButtonClick() }
        return button
    }
}