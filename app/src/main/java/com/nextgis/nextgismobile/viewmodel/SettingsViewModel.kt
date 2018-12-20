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

package com.nextgis.nextgismobile.viewmodel

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.model.SettingsModel
import com.nextgis.nextgismobile.util.NonNullObservableField

class SettingsViewModel : ViewModel() {
    private var settingsModel: SettingsModel = SettingsModel()

    var analytics = NonNullObservableField(true)
    var keepScreen = NonNullObservableField(true)
    var roaming = NonNullObservableField(false)
    val mapPath = NonNullObservableField("external")
    val mapPathSummary = ObservableField("SD Card")

    var statusPanel = NonNullObservableField(true)
    var zoomButtons = NonNullObservableField(true)
    var favorites = NonNullObservableField(true)
    var measurement = NonNullObservableField(true)

    var fillColor = NonNullObservableField("D0021B")
    var strokeWidth = NonNullObservableField("4")
    var strokeColor = NonNullObservableField("D0021B")

    var scaleFormat = NonNullObservableField("")
    var scaleFormatSummary = ObservableField("")
    var coordinatesFormat = NonNullObservableField("0")
    var coordinatesFormatSummary = ObservableField("00.000000")
    var coordinatesPrecision = NonNullObservableField("6")
    var mapBackground = NonNullObservableField("bright")
    var mapBackgroundSummary = ObservableField("Bright")

    var locationAccuracy = NonNullObservableField("3")
    var locationAccuracySummary = ObservableField("")
    var locationTime = NonNullObservableField("10")
    var locationDistance = NonNullObservableField("5")
    var locationCount = NonNullObservableField("10")

    var filterEmissions = NonNullObservableField(true)
    var splitDaily = NonNullObservableField(true)
    var cloudSync = NonNullObservableField(true)
    var trackTime = NonNullObservableField("10")
    var trackDistance = NonNullObservableField("5")
    var trackCount = NonNullObservableField("10")
    var uuid = NonNullObservableField("0055AAFF")

    fun setup(activity: Activity) {
        settingsModel.context = activity
    }

    fun load() {
        analytics.set(settingsModel.getBoolean(SettingsModel.ANALYTICS))
        keepScreen.set(settingsModel.getBoolean(SettingsModel.KEEP_SCREEN_ON))
        roaming.set(settingsModel.getBoolean(SettingsModel.ROAMING_NETWORK))
        statusPanel.set(settingsModel.getBoolean(SettingsModel.STATUS_PANEL))
        mapPath.set(settingsModel.getString(SettingsModel.MAP_PATH))
        mapPathSummary.set(description(if (mapPath.get() == "external") R.string.external else R.string.internal))

        zoomButtons.set(settingsModel.getBoolean(SettingsModel.ZOOM_BUTTONS))
        favorites.set(settingsModel.getBoolean(SettingsModel.USE_FAVORITES))
        measurement.set(settingsModel.getBoolean(SettingsModel.MEASURE_TOOLS))

        fillColor.set(settingsModel.getString(SettingsModel.FILL_COLOR))
        strokeColor.set(settingsModel.getString(SettingsModel.STROKE_COLOR))
        strokeWidth.set(settingsModel.getInt(SettingsModel.STROKE_WIDTH).toString())

        scaleFormat.set(settingsModel.getString(SettingsModel.SCALE_FORMAT))
        scaleFormatSummary.set(description(scaleFormat.get(), R.array.scale_format_value, R.array.scale_format))
        coordinatesFormat.set(settingsModel.getString(SettingsModel.COORDINATE_FORMAT))
        coordinatesFormatSummary.set(description(coordinatesFormat.get(), R.array.coordinates_format_value, R.array.coordinates_format))
        coordinatesPrecision.set(settingsModel.getInt(SettingsModel.COORDINATE_PRECISION).toString())
        mapBackground.set(settingsModel.getString(SettingsModel.MAP_BACKGROUND))
        mapBackgroundSummary.set(description(if (mapBackground.get() == "bright") R.string.bright else R.string.dark))

        locationAccuracy.set(settingsModel.getString(SettingsModel.LOCATION_ACCURACY))
        locationAccuracySummary.set(description(locationAccuracy.get(), R.array.location_accuracy_value, R.array.location_accuracy))
        locationTime.set(settingsModel.getInt(SettingsModel.LOCATION_TIME).toString())
        locationDistance.set(settingsModel.getInt(SettingsModel.LOCATION_DISTANCE).toString())
        locationCount.set(settingsModel.getInt(SettingsModel.LOCATION_COUNT).toString())

        filterEmissions.set(settingsModel.getBoolean(SettingsModel.FILTER_EMISSIONS))
        splitDaily.set(settingsModel.getBoolean(SettingsModel.SPLIT_DAILY))
        cloudSync.set(settingsModel.getBoolean(SettingsModel.CLOUD_SYNC))
        trackTime.set(settingsModel.getInt(SettingsModel.LOCATION_TIME).toString())
        trackDistance.set(settingsModel.getInt(SettingsModel.LOCATION_DISTANCE).toString())
        trackCount.set(settingsModel.getInt(SettingsModel.LOCATION_COUNT).toString())
        uuid.set(settingsModel.getUid())
    }

    fun save() {
        settingsModel.saveBoolean(SettingsModel.ANALYTICS, analytics.get())
        settingsModel.saveBoolean(SettingsModel.KEEP_SCREEN_ON, keepScreen.get())
        settingsModel.saveBoolean(SettingsModel.ROAMING_NETWORK, roaming.get())
        settingsModel.saveString(SettingsModel.MAP_PATH, mapPath.get())

        settingsModel.saveBoolean(SettingsModel.STATUS_PANEL, statusPanel.get())
        settingsModel.saveBoolean(SettingsModel.ZOOM_BUTTONS, zoomButtons.get())
        settingsModel.saveBoolean(SettingsModel.USE_FAVORITES, favorites.get())
        settingsModel.saveBoolean(SettingsModel.MEASURE_TOOLS, measurement.get())

        settingsModel.saveString(SettingsModel.FILL_COLOR, fillColor.get())
        settingsModel.saveString(SettingsModel.STROKE_COLOR, strokeColor.get())
        settingsModel.saveInt(SettingsModel.STROKE_WIDTH, strokeWidth.get().toInt())

        settingsModel.saveString(SettingsModel.SCALE_FORMAT, scaleFormat.get())
        settingsModel.saveString(SettingsModel.COORDINATE_FORMAT, coordinatesFormat.get())
        settingsModel.saveInt(SettingsModel.COORDINATE_PRECISION, coordinatesPrecision.get().toInt())
        settingsModel.saveString(SettingsModel.MAP_BACKGROUND, mapBackground.get())

        settingsModel.saveString(SettingsModel.LOCATION_ACCURACY, locationAccuracy.get())
        settingsModel.saveInt(SettingsModel.LOCATION_TIME, locationTime.get().toInt())
        settingsModel.saveInt(SettingsModel.LOCATION_DISTANCE, locationDistance.get().toInt())
        settingsModel.saveInt(SettingsModel.LOCATION_COUNT, locationCount.get().toInt())

        settingsModel.saveBoolean(SettingsModel.FILTER_EMISSIONS, filterEmissions.get())
        settingsModel.saveBoolean(SettingsModel.SPLIT_DAILY, splitDaily.get())
        settingsModel.saveBoolean(SettingsModel.CLOUD_SYNC, cloudSync.get())
        settingsModel.saveInt(SettingsModel.TRACK_TIME, trackTime.get().toInt())
        settingsModel.saveInt(SettingsModel.TRACK_DISTANCE, trackDistance.get().toInt())
        settingsModel.saveInt(SettingsModel.TRACK_COUNT, trackCount.get().toInt())
    }

    private fun description(resource: Int): String? {
        return settingsModel.context?.getString(resource)
    }

    private fun description(value: String, values: Int, resource: Int): String? {
        settingsModel.context?.resources?.getStringArray(values)?.let { it ->
            val id = it.indexOf(value)
            settingsModel.context?.resources?.getStringArray(resource)?.let {
                return it[id]
            }
        }

        return ""
    }

    fun setCoordinatesFormat(value: String) {
        coordinatesFormat.set(value)
    }
}