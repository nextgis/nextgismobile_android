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

package com.nextgis.nextgismobile.data

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.annotation.DrawableRes
import android.support.annotation.StringRes
import com.nextgis.maplib.Layer
import com.nextgis.maplib.Object
import com.nextgis.nextgismobile.BR
import com.nextgis.nextgismobile.R

class Layer(val id: Int, val title: String, val geometryType: Int, visible: Boolean, val handle: Layer) : BaseObservable() {
    val geometryTypeStr: Int
        @StringRes
        get() {
            return when (geometryType) {
                Object.Type.RASTER_TMS.code -> R.string.raster
                else -> R.string.not_implemented
            }
        }

    val geometryIcon: Int
        @DrawableRes
        get() {
            return when (geometryType) {
                Object.Type.RASTER_TMS.code -> R.drawable.ic_grid
                else -> R.drawable.indicator_dot_grey
            }
        }

    var visible = visible
        set(value) {
            field = value
            handle.visible = value
            notifyPropertyChanged(BR.visibility)
            notifyPropertyChanged(BR.tint)
        }

    @get:Bindable
    val visibility
        get() = if (visible) R.drawable.ic_eye else R.drawable.ic_eye_off

    @get:Bindable
    val tint
        get() = if (visible) R.color.colorPrimary else R.color.grey
}