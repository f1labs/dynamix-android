package com.dynamix.modsign.comparator

import com.dynamix.modsign.comparator.ModSignComparatorExtensions.toInt

class IntComparator: ModSignComparator {

    override fun equals(compareValue: Any, compareWith: Any): Boolean {
        return compareValue.toInt() == compareWith.toInt()
    }

    override fun greaterThan(compareValue: Any, compareWith: Any): Boolean {
        return compareValue.toInt() > compareWith.toInt()
    }

    override fun lessThan(compareValue: Any, compareWith: Any): Boolean {
        return compareValue.toInt() < compareWith.toInt()
    }

    override fun greaterThanEquals(compareValue: Any, compareWith: Any): Boolean {
        return compareValue.toInt() >= compareWith.toInt()
    }

    override fun lessThanEquals(compareValue: Any, compareWith: Any): Boolean {
        return compareValue.toInt() <= compareWith.toInt()
    }
}
