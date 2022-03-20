package com.dynamix.formbuilder.fields

import android.content.Context
import android.view.View
import android.widget.Button
import com.dynamix.formbuilder.R
import com.google.android.material.button.MaterialButton

/**
 * Created by user on 16/12/2021.
 */
// TODO(fix after major refactor)
object DynamixCustomButtonFieldView {

    fun button(
            ctx: Context,
            onSubmitButtonClick: () -> Unit,
    ): View {
        val button = MaterialButton(ctx, null, R.attr.geFmSubmitButtonStyle)
        button.text = ctx.getString(R.string.dynamix_action_proceed)
        button.setOnClickListener { onSubmitButtonClick() }
        return button
    }

    fun button(
            ctx: Context,
            submitButton: String?,
            onSubmitButtonClick: () -> Unit,
    ): View {
        val button: Button = MaterialButton(
                ctx,
                null,
                R.attr.geFmSubmitButtonStyle,
        )
        button.text = submitButton ?: ctx.getString(R.string.dynamix_action_submit)
        button.setOnClickListener { onSubmitButtonClick() }
        return button
    }

    fun cancelButton(
            ctx: Context,
            onCancelButtonClick: () -> Unit,
    ): View {
        val button = MaterialButton(ctx, null, R.attr.geFmCancelButtonStyle)
        button.setOnClickListener { onCancelButtonClick() }
        return button
    }

    fun cancelButton(
            ctx: Context,
            label: String?,
            onCancelButtonClick: () -> Unit,
    ): View {
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