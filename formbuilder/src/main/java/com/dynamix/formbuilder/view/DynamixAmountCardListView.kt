package com.dynamix.formbuilder.view

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dynamix.core.utils.DynamixConverter
import com.dynamix.core.utils.DynamixListItemSpacer
import com.dynamix.formbuilder.R
import com.dynamix.formbuilder.data.DynamixKeyValue
import com.google.android.material.button.MaterialButton

class DynamixAmountCardListView(
    context: Context,
    items: List<DynamixKeyValue>,
    clickListener: ItemClickListener
) {
    val listView: RecyclerView = RecyclerView(context)

    init {
        // setup recyclerView
        listView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        listView.addItemDecoration(
            DynamixListItemSpacer(
                -1, -1, DynamixConverter.dpToPx(context, 5), -1, false, true
            )
        )
        listView.adapter = Adapter(items, clickListener)
    }

    private inner class Adapter constructor(
        private val items: List<DynamixKeyValue>,
        private val clickListener: ItemClickListener
    ) : RecyclerView.Adapter<ItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            return ItemViewHolder(
                MaterialButton(
                    parent.context,
                    null,
                    R.attr.geFmAmountCardButtonStyle
                )
            )
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            holder.bind(items[position], clickListener)
        }

        override fun getItemCount(): Int {
            return items.size
        }
    }

    private inner class ItemViewHolder(private val button: MaterialButton) :
        RecyclerView.ViewHolder(button) {

        init {
            val context = button.context
            button.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                DynamixConverter.dpToPx(context, 42)
            )
        }

        fun bind(item: DynamixKeyValue, listener: ItemClickListener) {
            button.text = item.value
            button.setOnClickListener { listener.onItemClick(item) }
        }
    }

    fun interface ItemClickListener {
        fun onItemClick(item: DynamixKeyValue)
    }
}