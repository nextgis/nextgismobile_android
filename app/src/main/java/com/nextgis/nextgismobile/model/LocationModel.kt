/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2019 NextGIS, info@nextgis.com
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

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.pawegio.kandroid.locationManager

class LocationModel {

    fun onStart(context: Context?, listener: LocationListener) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        context?.let {
            if (ContextCompat.checkSelfPermission(it, permission) == PackageManager.PERMISSION_GRANTED) {
                val manager = it.locationManager
                val provider = LocationManager.GPS_PROVIDER
                manager?.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, listener)
            }
        }
    }

    fun onStop(context: Context?, listener: LocationListener) {
        context?.let {
            val permission = Manifest.permission.ACCESS_FINE_LOCATION
            if (ContextCompat.checkSelfPermission(it, permission) == PackageManager.PERMISSION_GRANTED) {
                it.locationManager?.removeUpdates(listener)
            }
        }
    }

    companion object {
        const val MIN_TIME = 5000L
        const val MIN_DISTANCE = 1f
    }

}