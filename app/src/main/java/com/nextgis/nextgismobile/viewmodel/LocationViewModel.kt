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

package com.nextgis.nextgismobile.viewmodel

import androidx.lifecycle.ViewModel
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import com.nextgis.nextgismobile.model.LocationModel

class LocationViewModel : ViewModel(), LocationListener {
    private var listener: ((location: Location?) -> Unit)? = null
    var location: Location? = null

    override fun onLocationChanged(location: Location) {
        //TODO("Not yet implemented")
        listener?.invoke(location)
        this.location = location
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

//    fun onProviderEnabled(p0: String?) {
//        TODO("Not yet implemented")
//    }
//
//    fun onProviderDisabled(p0: String?) {
//        TODO("Not yet implemented")
//    }

//    fun onProviderEnabled(p0: String?) {
//    }
//
//    fun onProviderDisabled(p0: String?) {
//    }

    private var locationModel = LocationModel()

    fun onStart(context: Context?, listener: ((location: Location?) -> Unit)) {
        this.listener = listener
        locationModel.onStart(context, this)
    }

    fun onStop(context: Context?) {
        locationModel.onStop(context, this)
    }

}