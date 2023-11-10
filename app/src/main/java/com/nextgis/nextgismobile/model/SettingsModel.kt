/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2018 NextGIS, info@nextgis.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.nextgis.nextgismobile.model

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import android.annotation.SuppressLint
import android.provider.Settings


class SettingsModel {
    companion object {
//        const val PREF = "app_preferences"
//        const val LOGGED_IN = "logged_in"

        const val ANALYTICS = "collect_analytics"
        const val KEEP_SCREEN_ON = "keep_screen_on"
        const val ROAMING_NETWORK = "roaming_on"
        const val MAP_PATH = "data_storage"

        const val STATUS_PANEL = "status_panel"
        const val ZOOM_BUTTONS = "zoom_buttons"
        const val USE_FAVORITES = "use_favorites"
        const val MEASURE_TOOLS = "map_measurement"

        const val FILL_COLOR = "fill_color"
        const val STROKE_COLOR = "stroke_color"
        const val STROKE_WIDTH = "stroke_width"

        const val SCALE_FORMAT = "scale_format"
        const val COORDINATE_FORMAT = "coordinate_format"
        const val COORDINATE_PRECISION = "coordinate_precision"
        const val MAP_BACKGROUND = "map_background"

        const val LOCATION_ACCURACY = "location_accuracy"
        const val LOCATION_TIME = "location_time"
        const val LOCATION_DISTANCE = "location_distance"
        const val LOCATION_COUNT = "location_count"

        const val SYNC_PERIOD = "sync_period"
        const val SYNC_NOTIFICATION = "sync_notification"
        const val SYNC_ATTACHMENTS = "sync_attachments"
        const val SYNC_MAX_SIZE = "sync_max_size"

        const val FILTER_EMISSIONS = "filter_emissions"
        const val SPLIT_DAILY = "split_daily"
        const val CLOUD_SYNC = "cloud_sync"
        const val TRACK_TIME = "track_time"
        const val TRACK_DISTANCE = "track_distance"
        const val TRACK_COUNT = "track_count"
    }

    private lateinit var preferences: SharedPreferences
    var context: Context? = null
        set(value) {
            preferences = PreferenceManager.getDefaultSharedPreferences(value!!)
            field = value
        }

    fun getBoolean(key: String): Boolean {
        return preferences.getBoolean(key, true)
    }

    fun saveBoolean(key: String, value: Boolean) {
        preferences.edit().putBoolean(key, value).apply()
    }

    fun getString(key: String): String {
        return preferences.getString(key, "") ?: ""
    }

    fun saveString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun getInt(key: String, default: Int? = null): Int {
        return preferences.getInt(key, default ?: 0)
    }

    fun saveInt(key: String, value: Int) {
        preferences.edit().putInt(key, value).apply()
    }

    @SuppressLint("HardwareIds")
    fun getUid(): String {
        val uuid = Settings.Secure.getString(context?.contentResolver, Settings.Secure.ANDROID_ID)
        return String.format("%X", uuid.hashCode())
    }
}