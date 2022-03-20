package com.dynamix.core.adapter

import androidx.databinding.ViewDataBinding

fun interface RecyclerCallback<VM : ViewDataBinding, T> {
    fun bindData(binding: VM, item: T, listItems: List<T>)
}