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
import com.nextgis.maplib.Field
import com.nextgis.nextgismobile.R

class Field(var name: String, var alias: String, var type: Field.Type, var def: String?) {
    val typeIcon: Int
        @DrawableRes
        get() {
            return when (type) {
                Field.Type.INTEGER -> R.drawable.ic_numeric_1_box_outline
                Field.Type.REAL -> R.drawable.ic_numeric_1_dot_box_outline
                Field.Type.STRING -> R.drawable.ic_format_color_text
                Field.Type.DATE -> R.drawable.ic_clock_outline
                else -> R.drawable.ic_delete
            }
        }
}