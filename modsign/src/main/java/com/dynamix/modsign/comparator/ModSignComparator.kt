package com.dynamix.modsign.comparator

interface ModSignComparator {

    fun equals(compareValue: Any, compareWith: Any): Boolean {
        return false
    }
    fun greaterThan(compareValue: Any, compareWith: Any): Boolean {
        return false
    }
    fun greaterThanEquals(compareValue: Any, compareWith: Any): Boolean {
        return false
    }
    fun lessThanEquals(compareValue: Any, compareWith: Any): Boolean {
        return false
    }
    fun lessThan(compareValue: Any, compareWith: Any): Boolean {
        return false
    }
    fun equalsIgnoreCase(compareValue: Any, compareWith: Any): Boolean {
        return false
    }
    fun notEquals(compareValue: Any, compareWith: Any): Boolean {
        return false
    }
    fun notEqualsIgnoreCase(compareValue: Any, compareWith: Any): Boolean {
        return false
    }
}