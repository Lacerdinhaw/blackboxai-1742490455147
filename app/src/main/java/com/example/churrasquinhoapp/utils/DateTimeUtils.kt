package com.example.churrasquinhoapp.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateTimeUtils {
    private val locale = Locale("pt", "BR")

    // Date Formatters
    private val dateFormatter = SimpleDateFormat(Constants.DateFormats.DATE_FORMAT, locale)
    private val timeFormatter = SimpleDateFormat(Constants.DateFormats.TIME_FORMAT, locale)
    private val dateTimeFormatter = SimpleDateFormat(Constants.DateFormats.DATE_TIME_FORMAT, locale)
    private val dateTimeWithSecondsFormatter = SimpleDateFormat(Constants.DateFormats.DATE_TIME_FORMAT_WITH_SECONDS, locale)
    private val apiDateFormatter = SimpleDateFormat(Constants.DateFormats.API_DATE_FORMAT, locale)
    private val apiDateTimeFormatter = SimpleDateFormat(Constants.DateFormats.API_DATE_TIME_FORMAT, locale)

    /**
     * Date formatting
     */
    fun formatDate(date: Date): String {
        return dateFormatter.format(date)
    }

    fun formatTime(date: Date): String {
        return timeFormatter.format(date)
    }

    fun formatDateTime(date: Date): String {
        return dateTimeFormatter.format(date)
    }

    fun formatDateTimeWithSeconds(date: Date): String {
        return dateTimeWithSecondsFormatter.format(date)
    }

    fun formatApiDate(date: Date): String {
        return apiDateFormatter.format(date)
    }

    fun formatApiDateTime(date: Date): String {
        return apiDateTimeFormatter.format(date)
    }

    /**
     * Date parsing
     */
    fun parseDate(dateString: String): Date? {
        return try {
            dateFormatter.parse(dateString)
        } catch (e: Exception) {
            Logger.e("Error parsing date: $dateString", e)
            null
        }
    }

    fun parseDateTime(dateTimeString: String): Date? {
        return try {
            dateTimeFormatter.parse(dateTimeString)
        } catch (e: Exception) {
            Logger.e("Error parsing datetime: $dateTimeString", e)
            null
        }
    }

    fun parseApiDate(apiDateString: String): Date? {
        return try {
            apiDateFormatter.parse(apiDateString)
        } catch (e: Exception) {
            Logger.e("Error parsing API date: $apiDateString", e)
            null
        }
    }

    /**
     * Date calculations
     */
    fun getStartOfDay(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.time
    }

    fun getEndOfDay(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.time
    }

    fun getStartOfWeek(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        return getStartOfDay(calendar.time)
    }

    fun getEndOfWeek(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = getStartOfWeek(date)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        return getEndOfDay(calendar.time)
    }

    fun getStartOfMonth(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return getStartOfDay(calendar.time)
    }

    fun getEndOfMonth(date: Date = Date()): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
        return getEndOfDay(calendar.time)
    }

    /**
     * Date comparisons
     */
    fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun isToday(date: Date): Boolean {
        return isSameDay(date, Date())
    }

    fun isFuture(date: Date): Boolean {
        return date.after(Date())
    }

    fun isPast(date: Date): Boolean {
        return date.before(Date())
    }

    /**
     * Time calculations
     */
    fun getDaysBetween(startDate: Date, endDate: Date): Int {
        val diff = endDate.time - startDate.time
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
    }

    fun addDays(date: Date, days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_MONTH, days)
        return calendar.time
    }

    fun subtractDays(date: Date, days: Int): Date {
        return addDays(date, -days)
    }

    /**
     * Date ranges
     */
    fun getDateRange(startDate: Date, endDate: Date): List<Date> {
        val dates = mutableListOf<Date>()
        var currentDate = startDate

        while (!currentDate.after(endDate)) {
            dates.add(currentDate)
            currentDate = addDays(currentDate, 1)
        }

        return dates
    }

    /**
     * Relative time
     */
    fun getRelativeTimeSpanString(date: Date): String {
        val now = Date()
        val diff = now.time - date.time
        val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff)
        val days = TimeUnit.MILLISECONDS.toDays(diff)

        return when {
            seconds < 60 -> "Agora"
            minutes < 60 -> "$minutes minutos atrás"
            hours < 24 -> "$hours horas atrás"
            days < 7 -> "$days dias atrás"
            else -> formatDate(date)
        }
    }
}