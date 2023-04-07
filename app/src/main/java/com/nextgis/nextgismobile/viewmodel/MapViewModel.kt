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
import androidx.lifecycle.MutableLiveData
import com.nextgis.maplib.MapDocument
import com.nextgis.maplib.Object
import com.nextgis.nextgismobile.data.Layer
import com.nextgis.nextgismobile.data.RasterLayer
import com.nextgis.nextgismobile.data.VectorLayer
import com.nextgis.nextgismobile.model.MapModel

class MapViewModel : ViewModel() {
    private var mapModel = MapModel()
    private var map: MapDocument? = null
    var layers = MutableLiveData<ArrayList<Layer>>()

    fun init(context: Context) {
        mapModel.init(context)
    }

    fun load(context : Context): MapDocument? {
        if (map == null)
            map = mapModel.load(context)
        return map
    }

    fun getLayers() {
        val data = arrayListOf<Layer>()
        map?.let {
            for (i in 0 until it.layerCount) {
                it.getLayer(i)?.let { layer ->
                    val wrapper: Layer
                    when {
                        Object.isRaster(layer.dataSource.type) -> wrapper = RasterLayer(i, layer)
                        Object.isFeatureClass(layer.dataSource.type) -> wrapper = VectorLayer(i, layer)
                        else -> wrapper = Layer(i, layer)
                    }
                    data.add(wrapper)
                }
            }
        }
        layers.value = data
    }
}