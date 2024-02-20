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

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.graphics.Color
import android.os.Build
import com.nextgis.maplib.*

class MapModel {
    fun init(context: Context) {
        API.init(context)
    }

    fun load(context : Context): MapDocument? {
        val map = API.getMap("main")
        map?.let {

            val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)

//            var reduceFactor = 1.0
//            val totalRam = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                memoryInfo.totalMem / (1024 * 1024)
//            } else {
//                512
//            }
//
//            if(totalRam < 1024) {
//                reduceFactor = 2.0
//            }
//
//            val options = mapOf(
//                "ZOOM_INCREMENT" to "-1", // Add extra to zoom level corresponding to scale
//                "VIEWPORT_REDUCE_FACTOR" to reduceFactor.toString() // Reduce viewport width and height to decrease memory usage
//            )
//
//            it.setOptions(options)

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

            addExamplePointsLayerTo(it)// example of add points - not workdd
            it.save()
        }
        return map
    }

    private fun addOSMTo(map: MapDocument) {
        val dataDir = API.getDataDirectory()
        if (dataDir != null) {
            val bbox = Envelope(MIN_X, MAX_X, MIN_Y, MAX_Y)
            val baseMap = dataDir.createTMS(OSM_NAME, OSM_URL, 3857, 0, 18, bbox, bbox, 14)
            if (baseMap != null) {
                map.addLayer("OSM", baseMap)
                map.save()
            } else {
                // todo message about map null and cannot work
            }
        }
    }

    private fun addExamplePointsLayerTo(map: MapDocument) {
        // Get or create data store
        val dataStore = API.getStore("store")
        if(dataStore != null) {
            // Create points feature class

            val options = mapOf(
                "CREATE_OVERVIEWS" to "ON",
                "ZOOM_LEVELS" to "2,3,4,5,6,7,8,9,10,11,12,13,14"
            )

            val fields = listOf(
                Field("long", "long", Field.Type.REAL),
                Field("lat", "lat", Field.Type.REAL),
                Field("datetime", "datetime", Field.Type.DATE, "CURRENT_TIMESTAMP"),
                Field("name", "name", Field.Type.STRING)
            )

            val pointsFC = dataStore.createFeatureClass("points", Geometry.Type.POINT, fields, options)
            if(pointsFC != null) {

                data class PtCoord(val name: String, val x: Double, val y: Double)

                // Add geodata to points feature class from https://en.wikipedia.org
                val coordinates = listOf(
                    PtCoord("Moscow", 37.616667, 55.75),
                    PtCoord("London", -0.1275, 51.507222),
                    PtCoord("Washington", -77.016389, 38.904722),
                    PtCoord("Beijing", 116.383333, 39.916667)
                )

                val coordTransform = CoordinateTransformation.new(4326, 3857)

                for(coordinate in coordinates) {
                    val feature = pointsFC.createFeature()
                    if(feature != null) {
                        val geom = feature.createGeometry() as? GeoPoint
                        if(geom != null) {
                            val point = Point(coordinate.x, coordinate.y)
                            val transformPoint = coordTransform.transform(point)
                            geom.setCoordinates(transformPoint)
                            feature.geometry = geom
                            feature.setField(0, coordinate.x)
                            feature.setField(1, coordinate.y)
                            feature.setField(3, coordinate.name)
                            pointsFC.insertFeature(feature)
                        }
                    }
                }

                // Create layer from points feature class
                val pointsLayer = map.addLayer("Points", pointsFC)
                if(pointsLayer != null) {

                    // Set layer style
                    pointsLayer.styleName = "pointsLayer"
                    val style = pointsLayer.style
                    style.setString("color", colorToHexString(Color.rgb(0, 190,120)))
                    style.setDouble("size", 20.0)
                    style.setInteger("type", 6) // Star symbol

                    pointsLayer.style = style
                }
            }
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