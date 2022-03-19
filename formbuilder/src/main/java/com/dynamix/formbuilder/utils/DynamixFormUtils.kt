package com.dynamix.formbuilder.utils

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.dynamix.core.extensions.colorFromTheme
import com.dynamix.core.extensions.dp
import com.dynamix.core.extensions.getSolidRoundedRect
import com.dynamix.core.extensions.themeTintedDrawable
import com.dynamix.core.utils.DynamixAmountFormatUtil
import com.dynamix.core.utils.DynamixResourceUtils
import com.dynamix.formbuilder.R

fun TextView.setAmount(amount: String?, isVisible: Boolean = true) {
    if (isVisible) {
        val formattedAmount =
            context.getString(
                R.string.dynamix_default_amount_format,
                DynamixAmountFormatUtil.formatAmount(amount)
            )
        val builder = SpannableStringBuilder(formattedAmount)
        builder.setSpan(RelativeSizeSpan(0.70f), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(
            ForegroundColorSpan(
                DynamixResourceUtils.getColor(
                    context,
                    R.color.material_on_surface_emphasis_medium
                )
            ),
            0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        text = builder
    } else {
        text = "XXX.XX"
    }
}

fun getAmountSpannable(context: Context, amount: String?): SpannableStringBuilder {
    // TODO (integrate currency code in params)
    val formattedAmount =
        context.getString(
            R.string.dynamix_default_amount_format,
            DynamixAmountFormatUtil.formatAmount(amount)
        )
    val builder = SpannableStringBuilder(formattedAmount)
    builder.setSpan(RelativeSizeSpan(0.70f), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    builder.setSpan(
        ForegroundColorSpan(
            DynamixResourceUtils.getColor(
                context,
                R.color.material_on_surface_emphasis_medium
            )
        ),
        0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return builder
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