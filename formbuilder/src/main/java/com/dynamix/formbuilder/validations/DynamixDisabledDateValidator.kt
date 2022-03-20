package com.dynamix.formbuilder.validations

import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import kotlinx.parcelize.Parcelize
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneOffset

@Parcelize
class DynamixDisabledDateValidator(private val disabledDays: List<DayOfWeek>) : DateValidator {

    override fun isValid(date: Long): Boolean {
        val currentDay = Instant.ofEpochMilli(date).atOffset(ZoneOffset.UTC).dayOfWeek
        return !isDayDisabled(currentDay)
    }

    private fun isDayDisabled(day: DayOfWeek): Boolean {
        var isDisabled = false
        for (disabledDay in disabledDays) {
            if (disabledDay == day) {
                isDisabled = true
                break
            }
        }
        return isDisabled
    }

    override fun describeContents(): Int {
        return 0
    }
}