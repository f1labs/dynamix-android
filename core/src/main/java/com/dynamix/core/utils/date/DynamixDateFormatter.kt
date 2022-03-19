package com.dynamix.core.utils.date

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DynamixDateFormatter {
    private val availableDateFormats = mutableListOf<String>()

    init {
        availableDateFormats.add(DynamixDateUtils.dateFormat1)
        availableDateFormats.add(DynamixDateUtils.dateFormat2)
        availableDateFormats.add(DynamixDateUtils.dateFormat3)
        availableDateFormats.add(DynamixDateUtils.dateFormat4)
        availableDateFormats.add(DynamixDateUtils.dateFormat5)
        availableDateFormats.add(DynamixDateUtils.dateFormat6)
        availableDateFormats.add(DynamixDateUtils.dateFormat7)
        availableDateFormats.add(DynamixDateUtils.dateFormat8)
        availableDateFormats.add(DynamixDateUtils.dateFormat9)
        availableDateFormats.add(DynamixDateUtils.dateFormat10)
        availableDateFormats.add(DynamixDateUtils.dateFormat11)
        availableDateFormats.add(DynamixDateUtils.dateFormat12)
        availableDateFormats.add(DynamixDateUtils.dateFormat13)
        availableDateFormats.add(DynamixDateUtils.dateFormat14)
        availableDateFormats.add(DynamixDateUtils.dateFormat15)
        availableDateFormats.add(DynamixDateUtils.dateFormat16)
        availableDateFormats.add(DynamixDateUtils.dateFormat17)
        availableDateFormats.add(DynamixDateUtils.dateFormat18)
        availableDateFormats.add(DynamixDateUtils.dateFormat19)
        availableDateFormats.add(DynamixDateUtils.dateFormat20)
        availableDateFormats.add(DynamixDateUtils.dateFormat21)
        availableDateFormats.add(DynamixDateUtils.dateFormat22)
    }

    fun getFormattedDate(value: String, requiredDateFormat: String): String {
        for (dateFormat in availableDateFormats) {
            val formattedDate = formatDate(dateFormat, value, requiredDateFormat)
            if (formattedDate != null) {
                return formattedDate
            }
        }
        return value
    }

    private fun formatDate(dateFormat: String, value: String, formatAfterParsed: String): String? {
        return try {
            LocalDate.parse(value, DateTimeFormatter.ofPattern(dateFormat))
                .format(DateTimeFormatter.ofPattern(formatAfterParsed))
        } catch (e: Exception) {
            null
        }
    }

    fun daysDifference(value: String): Long {
        for (dateFormat in availableDateFormats) {
            val localDate = formatDate(dateFormat, value)
            if (localDate != null) {
                val currentDate = LocalDate.now()
                return ChronoUnit.DAYS.between(currentDate, localDate)
            }
        }
        return 0
    }

    fun daysDifference(initialDate: String, endDate: String): Long {
        var formattedDate1: LocalDate? = null
        var formattedDate2: LocalDate? = null
        for (dateFormat in availableDateFormats) {
            formattedDate1 = formatDate(dateFormat, initialDate)
            if (formattedDate1 != null) {
                break
            }
        }
        for (dateFormat in availableDateFormats) {
            formattedDate2 = formatDate(dateFormat, endDate)
            if (formattedDate2 != null) {
                break
            }
        }
        if (formattedDate1 != null && formattedDate2 != null) {
            return ChronoUnit.DAYS.between(formattedDate1, formattedDate2)
        }
        return 0
    }

    private fun formatDate(dateFormat: String, value: String): LocalDate? {
        return try {
            LocalDate.parse(value, DateTimeFormatter.ofPattern(dateFormat))
        } catch (e: Exception) {
            null
        }
    }

}