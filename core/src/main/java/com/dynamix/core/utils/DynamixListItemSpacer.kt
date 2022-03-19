package com.dynamix.core.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * Use [DynamixListItemSpacerDecoration] instead, as it provides more customization for each items
 */
@Deprecated("This will be removed in future")
class DynamixListItemSpacer(
    private val start: Int,
    private val top: Int,
    private val end: Int,
    private val bottom: Int,
    private val shouldIgnoreFirst: Boolean = true,
    private val shouldIgnoreLast: Boolean = true
) : ItemDecoration() {

    constructor(
        all: Int,
        shouldIgnoreFirst: Boolean = true,
        shouldIgnoreLast: Boolean = true
    ) : this(
        all,
        all,
        all,
        all,
        shouldIgnoreFirst,
        shouldIgnoreLast
    )

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (shouldIgnoreFirst && parent.getChildAdapterPosition(view) == 0) {
            return
        }
        if (shouldIgnoreLast && parent.getChildAdapterPosition(view) == parent.adapter!!.itemCount - 1) {
            return
        }
        if (start != -1) {
            outRect.left = start
        }
        if (top != -1) {
            outRect.top = top
        }
        if (end != -1) {
            outRect.right = end
        }
        if (bottom != -1) {
            outRect.bottom = bottom
        }
    }
}