package com.dynamix.core.utils

import android.widget.Filter

/**
 * Created by user on 1/11/21
 */
class DynamixListItemFilter<T>(
    private val originalList: List<T>,
    private val callback: FilterCallback<T>
) : Filter() {

    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val tempList = mutableListOf<T>()
        if (constraint == null || constraint.isEmpty()) {
            tempList.addAll(originalList)
        } else {
            val filterPattern = constraint.toString().lowercase().trim { it <= ' ' }
            for (item in originalList) {
                if (callback.getPredicate(item, filterPattern)) {
                    tempList.add(item)
                }
            }
        }
        val results = FilterResults()
        results.count = tempList.size
        results.values = tempList
        return results
    }

    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        if (results.values != null && results.count != 0) {
            val filteredList: List<T> = results.values as List<T>
            callback.publishResults(filteredList)
        } else {
            callback.publishResults(listOf())
        }
    }

    interface FilterCallback<T> {
        fun getPredicate(item: T, pattern: String): Boolean
        fun publishResults(items: List<T>)
    }
}