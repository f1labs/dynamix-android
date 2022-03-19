package com.dynamix.modsign.comparator

import java.lang.UnsupportedOperationException

class StringComparator: ModSignComparator {


    override fun equals(compareValue: Any, compareWith: Any): Boolean {
        compareValue as String
        compareWith as String

        return compareValue == compareWith
    }

    override fun greaterThan(compareValue: Any, compareWith: Any): Boolean {
        throw UnsupportedOperationException("Greater than is not supported for String")
    }

    override fun greaterThanEquals(compareValue: Any, compareWith: Any): Boolean {
        TODO("Not yet implemented")
    }

    override fun lessThanEquals(compareValue: Any, compareWith: Any): Boolean {
        TODO("Not yet implemented")
    }

    override fun lessThan(compareValue: Any, compareWith: Any): Boolean {
        throw UnsupportedOperationException("Less than is not supported for String")
    }

    override fun equalsIgnoreCase(compareValue: Any, compareWith: Any): Boolean {
        compareValue as String
        compareWith as String

        return compareValue.equals(compareWith, ignoreCase = true)
    }

    override fun notEquals(compareValue: Any, compareWith: Any): Boolean {
        compareValue as String
        compareWith as String

        return compareValue != compareWith
    }

    override fun notEqualsIgnoreCase(compareValue: Any, compareWith: Any): Boolean {
        compareValue as String
        compareWith as String

        return !compareValue.equals(compareWith, ignoreCase = true)
    }

}