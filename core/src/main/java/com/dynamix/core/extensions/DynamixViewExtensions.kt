package com.dynamix.core.extensions

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.dynamix.core.R
import com.dynamix.core.utils.DynamixAmountFormatUtil.formatAmount
import com.dynamix.core.utils.DynamixConverter
import com.dynamix.core.utils.DynamixResourceUtils
import com.dynamix.core.utils.DynamixViewUtils
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.textfield.TextInputLayout

/**
 * Created by user on 14/09/2021.
 */

fun CircularProgressIndicator.setVisible(isVisible: Boolean) {
    if (isVisible) {
        show()
    } else {
        hide()
    }
}

fun TextView.setDrawable(
    @DrawableRes drawable: Int,
    color: Int? = null,
    heightDp: Int = 24,
    widthDp: Int = 24,
    gravity: Int = Gravity.START,
) {
    DynamixViewUtils.setTextViewDrawable(
        this,
        heightDp,
        widthDp,
        color,
        gravity,
        context,
        ContextCompat.getDrawable(context, drawable),
    )
}

fun TextView.setDrawable(
    drawable: Drawable,
    color: Int? = null,
    heightDp: Int = 24,
    widthDp: Int = 24,
    gravity: Int = Gravity.START,
) {
    DynamixViewUtils.setTextViewDrawable(
        this,
        heightDp,
        widthDp,
        color,
        gravity,
        context,
        drawable,
    )
}

fun TextView.updateDrawable(
    @DrawableRes drawable: Int,
    color: Int? = null,
    heightDp: Int = 24,
    widthDp: Int = 24,
    gravity: Int = Gravity.START,
) {
    val textDrawable = ContextCompat.getDrawable(context, drawable)!!
    if (color != null) {
        DrawableCompat.setTint(textDrawable, color)
    }
    if (heightDp != -1 && widthDp != -1) {
        textDrawable.setBounds(
            0, 0,
            DynamixConverter.dpToPx(context, widthDp),
            DynamixConverter.dpToPx(context, heightDp)
        )
        if (gravity == Gravity.START) {
            setCompoundDrawables(
                textDrawable,
                compoundDrawables[1],
                compoundDrawables[2],
                compoundDrawables[3]
            )
        } else if (gravity == Gravity.END) {
            setCompoundDrawables(
                compoundDrawables[0],
                compoundDrawables[1],
                textDrawable,
                compoundDrawables[3]
            )
        }
    } else {
        if (gravity == Gravity.START) {
            setCompoundDrawablesWithIntrinsicBounds(
                textDrawable,
                compoundDrawables[1],
                compoundDrawables[2],
                compoundDrawables[3]
            )
        } else if (gravity == Gravity.END) {
            setCompoundDrawablesWithIntrinsicBounds(
                compoundDrawables[0],
                compoundDrawables[1],
                textDrawable,
                compoundDrawables[3]
            )
        }
    }
}

fun TextInputLayout.notEmptyValidation(
    message: String
): Boolean {
    return if (editText?.text?.toString()?.isNotEmpty() == true) {
        true
    } else {
        isErrorEnabled = true
        error = message
        editText?.requestFocus()
        false
    }
}

/**
 * [TextView] extensions
 */
fun TextView.setTextAppearanceFromTheme(@AttrRes attr: Int) {
    setTextAppearanceCompat(context.resolveThemeAttribute(attr))
}

fun TextView.setTextAppearanceCompat(@StyleRes style: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        setTextAppearance(style)
    } else {
        setTextAppearance(context, style)
    }
}

fun TextView.setText(value: String, isVisible: Boolean = true) {
    text = if (isVisible) {
        value
    } else {
        "XX.X"
    }
}

/**
 * Tints the [ImageView] with primary color
 */
fun ImageView.tintWithPrimary() {
    setColorFilter(
        context.colorFromTheme(R.attr.colorPrimary), PorterDuff.Mode.SRC_IN
    )
}

/**
 * [MaterialCardView] extensions, this is only managed for bottom action cardViews, need to manage for other
 * views as well
 */
fun MaterialCardView.setShadow(
    shadowColor: Int = ColorUtils.setAlphaComponent(
        context.colorFromTheme(R.attr.colorOnSurface),
        117
    ),
    cornerRadiusDp: Float = 16f,
    elevationDp: Float = 2f,
    shadowGravity: Int = Gravity.BOTTOM,
    @ColorRes backgroundColorResource: Int = 0
) {
    // setup cardView for setting custom drawable
    setCardBackgroundColor(Color.TRANSPARENT)
    cardElevation = 0f
    setBackgroundColor(
        context.colorFromTheme(R.attr.colorSurface)
    )
    val firstLayer = 0
    val ratioTopBottom = 3
    val defaultRatio = 2

    if (background == null && backgroundColorResource == 0) {
        throw RuntimeException("Pass backgroundColorResource or use setBackground")
    }

    if (background != null && background !is ColorDrawable) {
        throw RuntimeException(
            "${background::class.java.name} " +
                    "is not supported, set background as " +
                    "ColorDrawable or pass background as a resource"
        )
    }

    val cornerRadiusValue = cornerRadiusDp.dp(context)
    val elevationValue = elevationDp.dp(context)

    val backgroundColor = if (backgroundColorResource != 0) {
        ContextCompat.getColor(context, backgroundColorResource)
    } else {
        (background as ColorDrawable).color
    }

    // Only managed for bottom card view
    val outerRadius = floatArrayOf(
        cornerRadiusValue,
        cornerRadiusValue,
        cornerRadiusValue,
        cornerRadiusValue,
        0f,
        0f,
        0f,
        0f
    )

    val directionOfY = when (shadowGravity) {
        Gravity.CENTER -> 0
        Gravity.TOP -> elevationValue / ratioTopBottom
        Gravity.BOTTOM -> elevationValue / ratioTopBottom
        else -> elevationValue / defaultRatio // Gravity.LEFT & Gravity.RIGHT
    }

    val directionOfX = when (shadowGravity) {
        Gravity.START -> -1 * elevationValue / ratioTopBottom
        Gravity.END -> elevationValue / ratioTopBottom
        else -> 0
    }

    val shapeDrawable = ShapeDrawable()
    shapeDrawable.paint.color = backgroundColor
    shapeDrawable.paint.setShadowLayer(
        elevationValue,
        directionOfX.toFloat(),
        directionOfY.toFloat(),
        shadowColor
    )
    shapeDrawable.shape = RoundRectShape(outerRadius, null, null)

    when (Build.VERSION.SDK_INT) {
        in Build.VERSION_CODES.BASE..Build.VERSION_CODES.O_MR1 -> setLayerType(
            View.LAYER_TYPE_SOFTWARE,
            shapeDrawable.paint
        )
    }

    val drawable = LayerDrawable(arrayOf(shapeDrawable))
    drawable.setLayerInset(
        firstLayer,
        0,
        0,
        0,
        0
    )
    background = drawable
}

/**
 * [EditText] end icon touch listener, note endDrawable should be set
 * @param onClickStart callback to invoke when user starts to touch the icon
 * @param onClickEnd callback to invoke when user ends the touch
 */

@SuppressLint("ClickableViewAccessibility")
fun EditText.setEndIconTouchListener(
    onClickStart: () -> Unit,
    onClickEnd: () -> Unit,
    default: (() -> Unit)? = null,
) {
    setOnTouchListener { _, event ->
        if (isClickWithinRightDrawableBound(event)) {
            if (event.action == MotionEvent.ACTION_DOWN) {
                onClickStart()
                return@setOnTouchListener true
            } else if (event.action == MotionEvent.ACTION_UP) {
                onClickEnd()
                return@setOnTouchListener true
            }
        }
        default?.invoke()
        return@setOnTouchListener false
    }
}

/**
 * checks if the given motion event is within the right drawables bounds, both in x and y axis
 */
fun EditText.isClickWithinRightDrawableBound(event: MotionEvent): Boolean {
    val bounds = compoundDrawables[2].bounds
    val isXBound =
        event.x >= (right - paddingRight - bounds.width()) && event.x <= (right - paddingRight)
    val isYBound =
        event.y >= (bottom - paddingBottom - bounds.height()) && event.y <= (bottom - paddingBottom)
    return isXBound && isYBound
}

/**
 * Makes the [EditText] non editable but still clickable
 */
fun EditText.nonEditable() {
    isFocusable = false
    isFocusableInTouchMode = false
    isLongClickable = false
    isCursorVisible = false
}

///**
// * [AmountView] extensions
// */
//fun AmountView.setAmount(currency: String? = null, amountValue: String?) {
//    prefix = currency ?: "NPR"
//    amount = AmountFormatUtil.formatAmount(amountValue)
//}

/* [Fragment] extensions
 */
fun Fragment.dp(value: Int): Int {
    return value.dp(requireContext())
}

/**
 * [AutoCompleteTextView] extensions to make it more like spinner
 */
fun AutoCompleteTextView.selectedItemPosition(): Int {
    var index = -1
    for (i in 0 until adapter.count) {
        if (adapter.getItem(i) == text.toString()) {
            index = i
            break
        }
    }
    return index
}

fun AutoCompleteTextView.itemIndexFor(item: String): Int {
    var index = -1
    for (i in 0 until adapter.count) {
        if (adapter.getItem(i) == item) {
            index = i
            break
        }
    }
    return index
}

fun AutoCompleteTextView.setSelectedIndex(position: Int) {
    val value = adapter.getItem(position) as String
    setText(value, false)
}

fun View.toAutoCompleteTextView(): AutoCompleteTextView {
    return (this as TextInputLayout).editText as AutoCompleteTextView
}