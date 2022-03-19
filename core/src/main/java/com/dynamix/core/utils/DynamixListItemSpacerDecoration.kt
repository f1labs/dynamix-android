package com.dynamix.core.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by user on 10/11/2021.
 */
class DynamixListItemSpacerDecoration(
    private val start: Int = 0,
    private val top: Int = 0,
    private val end: Int = 0,
    private val bottom: Int = 0,
    private val handler: ((Int, Rect) -> Boolean)? = null,
) : RecyclerView.ItemDecoration() {

    constructor(all: Int, handler: ((Int, Rect) -> Boolean)? = null) : this(
        all,
        all,
        all,
        all,
        handler
    )

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(start, top, end, bottom)
        handler?.invoke(parent.getChildAdapterPosition(view), outRect)
    }
}