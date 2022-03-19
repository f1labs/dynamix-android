package com.dynamix.formbuilder.utils

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.dynamix.core.extensions.colorFromTheme
import com.dynamix.core.extensions.dp
import com.dynamix.core.extensions.getSolidRoundedRect
import com.dynamix.core.extensions.themeTintedDrawable
import com.dynamix.core.utils.DynamixCommonUtils.getColorFromAttribute
import com.dynamix.formbuilder.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

object DynamixFormHelper {

    fun setColorForDrawable(view: View, isDialog: Boolean) {
        // TODO(fix that after design)
        when (view) {
            is TextInputLayout -> {
                view.setEndIconTintList(ColorStateList.valueOf(view.context.colorFromTheme(R.attr.inputIconTint)))
            }
            is TextInputEditText -> {
                val drawable = view.compoundDrawables[2]
                if (drawable != null) {
                    drawable.setColorFilter(
                        getColorFromAttribute(view.getContext(), R.attr.inputIconTint),
                        PorterDuff.Mode.SRC_IN
                    )
                    view.setCompoundDrawables(null, null, drawable, null)
                }
            }
            is AppCompatAutoCompleteTextView -> {
                val drawable = view.compoundDrawables[2]
                if (drawable != null) {
                    drawable.setColorFilter(
                        getColorFromAttribute(view.getContext(), R.attr.inputIconTint),
                        PorterDuff.Mode.SRC_IN
                    )
                    view.setCompoundDrawables(null, null, drawable, null)
                }
            }
        }
    }

    fun addIconToEnd(
        textInputLayout: View,
        viewList: MutableList<View>? = null,
        @DrawableRes icon: Int,
        onClick: (() -> Unit)? = null,
    ): LinearLayout {
        val ctx = textInputLayout.context
        textInputLayout.layoutParams =
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)

        val selfNumberButton =
            ImageView(ctx).apply {
                background = ctx.getSolidRoundedRect(
                    bgColor = ctx.colorFromTheme(R.attr.primaryColor100),
                    strokeColor = ctx.colorFromTheme(R.attr.primaryColor200),
                )
                setImageDrawable(
                    ctx.themeTintedDrawable(icon, R.attr.colorPrimary)
                )
                val padding = 14.dp(ctx)
                setPadding(padding, padding, padding, padding)
                layoutParams = LinearLayout.LayoutParams(
                    48.dp(ctx),
                    48.dp(ctx)
                ).apply {
                    setMargins(8.dp(ctx), 0, 0, 0)
                }
                setOnClickListener {
                    onClick?.invoke()
                }
                viewList?.add(this)
            }

        // add the views
        val linearLayout = LinearLayout(ctx).apply {
            addView(textInputLayout)
            addView(selfNumberButton)
        }
        return linearLayout
    }

}