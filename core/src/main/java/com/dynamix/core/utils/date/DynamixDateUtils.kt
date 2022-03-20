package com.dynamix.core.utils.date

import com.dynamix.core.logger.AppLoggerProvider
import org.koin.java.KoinJavaComponent.inject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DynamixDateUtils {
    const val dateFormat1 = "dd-MM-yyyy HH:mm:ss"
    const val dateFormat2 = "dd-MM-yyyy"
    const val dateFormat3 = "yyyy-MM-dd"
    const val dateFormat4 = "MMM dd, yyyy"
    const val dateFormat5 = "MMM dd, yyyy"
    const val dateFormat6 = "MMM dd"
    const val dateFormat7 = "HH:mm:ss"
    const val dateFormat8 = "yyyy-MM-dd"
    const val dateFormat9 = "EEE, dd MMM yyyy"
    const val dateFormat10 = "yyyyMMddHHmmss"
    const val dateFormat11 = "yyyy-MM-dd HH:mm:ss"
    const val dateFormat12 = "EEE, d MMM yyyy HH:mm"
    const val dateFormat13 = "yyyy/MM/dd"
    const val dateFormat14 = "MM/dd/yyyy"
    const val dateFormat15 = "dd MMM yyyy,HH:mm a"
    const val dateFormat16 = "E, dd MMM"
    const val dateFormat17 = "yyyy-MM-dd HH:mm:ss"
    const val dateFormat18 = "HH:mm:ss"
    const val dateFormat19 = "HH:mm aa"
    const val dateFormat20 = "HH:mm"
    const val dateFormat21 = "E, dd MMM HH:mm a"
    const val dateFormat22 = "yyyy-MM-dd HH:mm:ss a"

    private val appLoggerProvider: AppLoggerProvider by inject(AppLoggerProvider::class.java)

    //In which you need put here
    val currentDateTime: String
        get() {
            val myCalendar = Calendar.getInstance()
            val myFormat = "dd-MM-yyyy HH:mm:ss" //In which you need put here
            val sdf = SimpleDateFormat(myFormat, Locale.UK)
            return sdf.format(myCalendar.time)
        }
    val currentDateTimeAmPm: String
        get() {
            val myCalendar = Calendar.getInstance()
            val myFormat = "E dd MM,yyyy hh:mm:ss a"
            val sdf = SimpleDateFormat(myFormat, Locale.UK)
            return sdf.format(myCalendar.time)
        }

    fun getFormattedDate(dateFormat: String, offset: Int): String {
        val myCalendar = Calendar.getInstance()
        myCalendar.add(Calendar.DATE, offset)
        val sdf = SimpleDateFormat(dateFormat, Locale.UK)
        return sdf.format(myCalendar.time)
    }

    fun getFormattedDate(dateFormat: String, date: Date): String {
        val sdf = SimpleDateFormat(dateFormat, Locale.UK)
        return sdf.format(date)
    }

    fun getFormattedDateFromKathmanduToUtc(dateFormat: String, date: Date): String {
        val formatPattern = dateFormat
        val sdf = SimpleDateFormat(dateFormat, Locale.UK)
        var T1: TimeZone
        var T2: TimeZone

        // set the Calendar of sdf to timezone T1
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kathmandu"));


        // set the Calendar of sdf to timezone T2
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(date)
    }

    val dateFormat: SimpleDateFormat
        get() = SimpleDateFormat(dateFormat3, Locale.UK)

    fun getDateFormat(dateFormat: String): SimpleDateFormat {
        return SimpleDateFormat(dateFormat, Locale.UK)
    }

    val date: String
        get() {
            val myCalendar = Calendar.getInstance()
            val myFormat = "dd-MM-yyyy"
            val sdf = SimpleDateFormat(myFormat, Locale.UK)
            return sdf.format(myCalendar.time)
        }

    fun getNepaliMonthString(month: Int): String {
        return when (month) {
            0 -> "Baisakh"
            1 -> "Jestha"
            2 -> "Ashar"
            3 -> "Shrawan"
            4 -> "Bhadra"
            5 -> "Ashoj"
            6 -> "Kartik"
            7 -> "Mangsir"
            8 -> "Poush"
            9 -> "Magh"
            10 -> "Falgun"
            11 -> "Chaitra"
            else -> "N/A"
        }
    }

    fun getRemainingDaysFromCurrentDate(date: String): String {
        val endDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S", Locale.UK)
        val currentDateFormat = SimpleDateFormat(dateFormat1, Locale.UK)
        return try {
            //Date currentDate = currentDateFormat.parse(getCurrentDateTime());
            val currentDate = Date()
            val endDate = endDateFormat.parse(date)!!
            val different = endDate.time - currentDate.time
            val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60
            val hoursInMilli = minutesInMilli * 60
            val daysInMilli = hoursInMilli * 24
            val elapsedDays = different / daysInMilli
            elapsedDays.toString()
        } catch (e: ParseException) {
            appLoggerProvider.debug(e.localizedMessage ?: "")
            "N/A"
        }
    }

    fun convertDate(invoiceDate: String): String {
        return try {
            val date = getDateFormat(dateFormat17).parse(invoiceDate)!!
            val d = getDateFormat(dateFormat2).format(date)
            println("date is $d")
            d
        } catch (e: ParseException) {
            invoiceDate
        }
    }

    fun convertDate(dateFormat: String, invoiceDate: String): String {
        return try {
            val date = getDateFormat(dateFormat).parse(invoiceDate)!!
            val d = getDateFormat(dateFormat2).format(date)
            println("date is $d")
            d
        } catch (e: ParseException) {
            invoiceDate
        }
    }

    fun isPastDate(dateFormat: String, date: String): Boolean {
        return try {
            val d = getDateFormat(dateFormat).parse(date)!!
            d.before(Date())
        } catch (e: ParseException) {
            false
        }
    }

    fun getDaysBetweenDates(currentDate: String, dateFromServer: String): Long {
        val serverDateFormat = SimpleDateFormat(dateFormat4, Locale.UK)
        val currentDateFormat = SimpleDateFormat(dateFormat1, Locale.UK)
        val startDate: Date
        val endDate: Date
        var numberOfDays: Long = 0
        try {
            startDate = currentDateFormat.parse(currentDate)!!
            val formattedDate = serverDateFormat.format(startDate)
            val formattedCurrentDate = serverDateFormat.parse(formattedDate)!!
            endDate = serverDateFormat.parse(dateFromServer)!!
            //  startDate = currentDateFormat.parse(currentDate);

            // DateUtils.getFormattedDate(DateUtils.dateFormat1, endDate);
            numberOfDays = getUnitBetweenDates(formattedCurrentDate, endDate, TimeUnit.DAYS)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return numberOfDays
    }

    private fun getUnitBetweenDates(startDate: Date, endDate: Date, unit: TimeUnit): Long {
        val timeDiff = endDate.time - startDate.time
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS)
    }

    fun isPastDateWithTime(dateFormat: String, date: String): Boolean {
        return try {
            val d = getDateFormat(dateFormat).parse(date)
            val currentDateValue =
                getDateFormat(dateFormat).parse(getFormattedDate(dateFormat, Date()))
            d.before(currentDateValue)
        } catch (e: ParseException) {
            false
        }
    }
}