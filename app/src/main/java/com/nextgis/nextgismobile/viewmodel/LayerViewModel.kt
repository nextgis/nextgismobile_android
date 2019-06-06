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

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nextgis.nextgismobile.data.Field
import com.nextgis.nextgismobile.data.VectorLayer

class LayerViewModel : ViewModel() {
    val vectorLayer = VectorLayer(0, null)
    var fields = MutableLiveData<ArrayList<Field>>()

    fun init() {
        fields.value = arrayListOf()
    }

    fun addField(field: Field) {
        val new = fields.value
        new?.add(field)
        fields.value = new
    }

    fun deleteField(field: Field) {
        val new = fields.value
        new?.remove(field)
        fields.value = new
    }
}