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

package com.nextgis.nextgismobile.fragment

 
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.nextgis.maplib.formatCoordinate
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.viewmodel.LocationViewModel


class LocationInfoFragment : Fragment() {
    private lateinit var locationModel: LocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationModel = ViewModelProvider(requireActivity()).get(LocationViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        locationModel.onStart(context) { onLocationChanged(it) }
    }

    override fun onStop() {
        super.onStop()
        locationModel.onStop(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.location_info, container, false)
        return view
    }

    private fun onLocationChanged(location: Location?) {
        location?.let {
            view?.findViewById<ImageView>(R.id.locationIcon)?.setImageResource(R.drawable.ic_gps_fixed)

            if (location.hasAccuracy()) {
                view?.findViewById<TextView>(R.id.accuracyText)?.text = getString(R.string.location_m_format).format(location.accuracy)
                view?.findViewById<ImageView>(R.id.accuracyIcon)?.setImageResource(R.drawable.ic_accuracy_on)
            } else {
                view?.findViewById<ImageView>(R.id.accuracyIcon)?.setImageResource(R.drawable.ic_accuracy)
            }

            // TODO: Add digits and format into settings
            view?.findViewById<TextView>(R.id.locationText)?.text = formatLocation(location.longitude, location.latitude, 2, Location.FORMAT_SECONDS)

            view?.findViewById<TextView>(R.id.signalSourceText)?.text = location.provider
            view?.findViewById<TextView>(R.id.speedText)?.text = formatSpeed(location.speed)
            val satelliteCount = location.extras?.getInt("satellites")
            view?.findViewById<TextView>(R.id.satCountText)?.text = satelliteCount.toString()

            view?.findViewById<TextView>(R.id.altText)?.text = getString(R.string.location_m_format).format(location.altitude)
        }
    }

    private fun formatSpeed(speed: Float): String {
        return getString(R.string.location_km_hr_format).format(3.6 * speed) // When converting m/sec to km/hr, divide the speed with 1000 and multiply the result with 3600.
    }

    private fun formatLocation(lon: Double, lat: Double, digits: Int, format: Int): String {
        val outLon = formatCoordinate(lon, format, digits)
        val outLat = formatCoordinate(lat, format, digits)
        return getString(R.string.location_coordinates).format(outLat, outLon)
    }
}