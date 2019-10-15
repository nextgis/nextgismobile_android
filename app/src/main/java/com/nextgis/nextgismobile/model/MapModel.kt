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

import android.content.Context
import com.nextgis.maplib.API
import com.nextgis.maplib.Envelope
import com.nextgis.maplib.MapDocument

class MapModel {
    fun init(context: Context) {
        API.init(context, API.generatePrivateKey())
    }

    fun load(): MapDocument? {
        val map = API.getMap("main")
        map?.let {
            it.setExtentLimits(MIN_X, MIN_Y, MAX_X, MAX_Y)
            var hasOSM = false
            for (i in 0 until it.layerCount) {
                it.getLayer(i)?.let { layer ->
                    if (layer.dataSource.name == OSM_NAME) {
                        hasOSM = true
                    }
                }
            }

            if (!hasOSM)
                addOSMTo(it)
        }
        return map
    }

    private fun addOSMTo(map: MapDocument) {
        val dataDir = API.getDataDirectory()
        if (dataDir != null) {
            val bbox = Envelope(MIN_X, MAX_X, MIN_Y, MAX_Y)
            val baseMap = dataDir.createTMS(OSM_NAME, OSM_URL, 3857, 0, 18, bbox, bbox, 14)
            map.addLayer("OSM", baseMap!!)
            map.save()
        }
    }

    companion object {
        const val OSM_NAME = "osm.wconn"
        const val OSM_URL = "http://tile.openstreetmap.org/{z}/{x}/{y}.png"
        const val MAX_X = 20037508.34
        const val MIN_X = -MAX_X
        const val MAX_Y = 20037508.34
        const val MIN_Y = -MAX_Y
    }

}