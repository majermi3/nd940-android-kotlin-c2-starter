package com.udacity.asteroidradar.utility

import android.content.Context
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.dto.PictureOfDay

object SharedPreferencesUtility {

    fun savePictureOfDayInPreferences(context: Context, pictureOfDay: PictureOfDay) {
        val sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putString(Constants.PREFERENCES_URL, pictureOfDay.url)
            putString(Constants.PREFERENCES_TITLE, pictureOfDay.title)
            putString(Constants.PREFERENCES_MEDIA_TYPE, pictureOfDay.mediaType)
            apply()
        }
    }

    fun getPictureOfDayFromPreferences(context: Context): PictureOfDay? {
        val sharedPref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        val pictureUrl = sharedPref.getString(Constants.PREFERENCES_URL, null)
        val pictureTitle = sharedPref.getString(Constants.PREFERENCES_TITLE, null)
        val pictureMediaType = sharedPref.getString(Constants.PREFERENCES_MEDIA_TYPE, null)

        return if(pictureUrl != null && pictureTitle != null && pictureMediaType != null) {
            PictureOfDay(pictureMediaType, pictureTitle, pictureUrl)
        } else {
            null
        }
    }
}