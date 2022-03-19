package com.dynamix.modsign.comparator

import com.dynamix.modsign.comparator.ModSignComparatorExtensions.toBoolean

class BooleanComparator: ModSignComparator {

    override fun equals(compareValue: Any, compareWith: Any): Boolean {
        return compareValue.toBoolean() == compareWith.toBoolean()
    }

}