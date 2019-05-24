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

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.nextgis.maplib.Layer
import com.nextgis.maplib.Object
import com.nextgis.nextgismobile.BR
import com.nextgis.nextgismobile.R
import kotlin.math.roundToInt

open class Layer(val id: Int, val handle: Layer) : BaseObservable() {
    val type = handle.dataSource.type

    val geometryTypeStr: Int
        @StringRes
        get() {
            return when (type) {
                Object.Type.RASTER_TMS.code -> R.string.raster
                else -> R.string.not_implemented
            }
        }

    val geometryIcon: Int
        @DrawableRes
        get() {
            return when (type) {
                Object.Type.RASTER_TMS.code -> R.drawable.ic_grid
                else -> R.drawable.appintro_indicator_dot_grey
            }
        }

    var visible = handle.visible
        set(value) {
            field = value
            handle.visible = value
            notifyPropertyChanged(BR.visibility)
            notifyPropertyChanged(BR.tint)
        }

    var minZoom = 0
        get() {
            val min = handle.minZoom.roundToInt()
            return if (min < 0) 0 else min
        }
        set(value) {
            field = value
            handle.minZoom = value.toFloat()
        }

    var maxZoom = 25
        get() {
            val max = handle.maxZoom.roundToInt()
            return if (max > 25) 25 else max
        }
        set(value) {
            field = value
            handle.maxZoom = value.toFloat()
        }

    @get:Bindable
    var title = handle.name
        set(value) {
            field = value
            handle.name = value
            notifyPropertyChanged(BR.title)
        }

    @get:Bindable
    val visibility
        get() = if (visible) R.drawable.ic_eye else R.drawable.ic_eye_off

    @get:Bindable
    val tint
        get() = if (visible) R.color.colorPrimary else R.color.grey

    val isRaster
        get() = Object.isRaster(type)

    protected fun getProperty(domain: String, name: String): String? {
        return handle.dataSource.getProperties(domain)[name]
    }

    protected fun setProperty(domain: String, name: String, value: String) {
        handle.dataSource.setProperty(name, value, domain)
    }
}