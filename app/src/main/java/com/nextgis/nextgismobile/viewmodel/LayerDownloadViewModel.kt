/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2020 NextGIS, info@nextgis.com
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
import com.nextgis.maplib.util.NonNullObservableField
import com.nextgis.nextgismobile.data.VectorLayer

class LayerDownloadViewModel : ViewModel() {
    val currentPage = NonNullObservableField(0)
    val vectorType = NonNullObservableField(true)
    val vectorLayer = VectorLayer(0, null)

    fun next() {
        // TODO real data
        vectorLayer.title = "Test layer"
        currentPage.set(currentPage.get() + 1)
    }

    fun changeType() {
        vectorType.set(!vectorType.get())
    }
}