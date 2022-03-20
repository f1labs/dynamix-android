package com.dynamix.core.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

class DynamixGenericRecyclerAdapter<T, VM : ViewDataBinding>(
    private var listItems: List<T>,
    private val layoutId: Int,
    private val bindingInterface: RecyclerCallback<VM, T>
) : RecyclerView.Adapter<DynamixGenericRecyclerAdapter<T, VM>.BindingHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
        return BindingHolder(v)
    }

    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
        val item = listItems[holder.adapterPosition]
        bindingInterface.bindData(holder.binding, item, listItems)
    }

    override fun getItemCount(): Int {
        return try {
            listItems.size
        } catch (e: Exception) {
            0
        }
    }

    fun refreshData(refreshedListItems: List<T>) {
        listItems = refreshedListItems
        notifyDataSetChanged()
    }

    inner class BindingHolder(view: View) : RecyclerView.ViewHolder(view) {
        var binding: VM = DataBindingUtil.bind(view)!!
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}