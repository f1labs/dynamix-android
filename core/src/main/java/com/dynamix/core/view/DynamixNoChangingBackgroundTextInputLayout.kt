package com.dynamix.core.view

import android.content.Context
import android.graphics.ColorFilter
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.textfield.TextInputLayout

/**
 * Created by shreejan.shrestha on 7/13/2017.
 */
class DynamixNoChangingBackgroundTextInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : TextInputLayout(context, attrs, defStyleAttr) {

    override fun setError(error: CharSequence?) {
        val defaultColorFilter = getBackgroundDefaultColorFilter()
        super.setError(error)
        //Reset EditText's background color to default.
        updateBackgroundColorFilter(defaultColorFilter)
    }

    override fun drawableStateChanged() {
        val defaultColorFilter = getBackgroundDefaultColorFilter()
        super.drawableStateChanged()
        //Reset EditText's background color to default.
        updateBackgroundColorFilter(defaultColorFilter)
    }

    /**
     * If [.getEditText] is not null & [.getEditText] is not null,
     * update the [ColorFilter] of [.getEditText].
     *
     * @param colorFilter [ColorFilter]
     */
    private fun updateBackgroundColorFilter(colorFilter: ColorFilter?) {
        if (editText != null && editText!!.background != null) editText!!.background.colorFilter =
            colorFilter
    }

    /**
     * Get the EditText's default background color.
     *
     * @return [ColorFilter]
     */
    private fun getBackgroundDefaultColorFilter(): ColorFilter? {
        var defaultColorFilter: ColorFilter? = null
        if (editText != null && editText!!.background != null) {
            defaultColorFilter =
                DrawableCompat.getColorFilter(
                    editText!!.background
                )
        }
        return defaultColorFilter
    }

    override fun setPasswordVisibilityToggleDrawable(icon: Drawable?) {
        super.setPasswordVisibilityToggleDrawable(icon)
    }
}