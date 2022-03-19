package com.dynamix.modsign.comparator

import com.dynamix.modsign.comparator.ModSignComparatorExtensions.toDouble

class DoubleComparator: ModSignComparator {

    override fun equals(compareValue: Any, compareWith: Any): Boolean {
        return compareValue.toDouble() == compareWith.toDouble()
    }

    override fun greaterThan(compareValue: Any, compareWith: Any): Boolean {
        return compareValue.toDouble() > compareWith.toDouble()
    }

    override fun lessThan(compareValue: Any, compareWith: Any): Boolean {
        return compareValue.toDouble() < compareWith.toDouble()
    }

    override fun greaterThanEquals(compareValue: Any, compareWith: Any): Boolean {
        return compareValue.toDouble() >= compareWith.toDouble()
    }

    override fun lessThanEquals(compareValue: Any, compareWith: Any): Boolean {
        return compareValue.toDouble() >= compareWith.toDouble()
    }

}