package com.dynamix.modsign.comparator

object ModSignComparatorExtensions {

    fun Any.toInt(): Int {
        if(this is String) {
            return this.toInt()
        }

        return this as Int
    }

    fun Any.toDouble(): Double {
        if(this is String) {
            return this.toDouble()
        }

        return this as Double
    }

    fun Any.toBoolean(): Boolean {
        if(this is String) {
            return this.toBoolean()
        }

        return this as Boolean
    }

}