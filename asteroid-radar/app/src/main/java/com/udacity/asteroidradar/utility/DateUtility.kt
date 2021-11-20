package com.udacity.asteroidradar.utility

import com.udacity.asteroidradar.Constants
import java.text.SimpleDateFormat
import java.util.*

object DateUtility {
    fun getTodayFormatted(): String{
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(calendar.time)
    }
}