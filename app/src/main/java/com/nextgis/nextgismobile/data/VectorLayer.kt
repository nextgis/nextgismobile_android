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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.nextgis.maplib.Geometry
import com.nextgis.maplib.Object
import com.nextgis.nextgismobile.R

class VectorLayer(id: Int, handle: com.nextgis.maplib.Layer?) :
    Layer(id, handle) {
    var geometryType = Geometry.Type.NONE

    override val typeStr: Int
        @StringRes
        get() {
            return when (type) { // TODO proper geometry type
                Object.Type.FC_GPKG.code -> R.string.vector
//                Object.Type.FC_GPKG.code -> {
//                    when ()
//                }
                else -> R.string.not_implemented
            }
        }

    override val typeIcon: Int
        @DrawableRes
        get() {
            return when (type) { // TODO proper geometry type
                Object.Type.FC_GPKG.code -> R.drawable.appintro_indicator_dot_grey
                else -> R.drawable.appintro_indicator_dot_grey
            }
        }

}