package com.dynamix.modsign.comparator

import java.lang.UnsupportedOperationException

class ModSignComparatorFactory<T> {

    fun getComparator(comparatorType: Class<T>): ModSignComparator {

        return when (comparatorType) {
            String::class.java -> {
                StringComparator()
            }
            Int::class.java -> {
                IntComparator()
            }
            Double::class.java -> {
                DoubleComparator()
            }
            Boolean::class.java -> {
                BooleanComparator()
            }
            else -> {
                throw UnsupportedOperationException("$comparatorType is not supported.")
            }
        }

    }

}