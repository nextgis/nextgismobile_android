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
import androidx.databinding.Bindable
import com.nextgis.maplib.FeatureClass
import com.nextgis.maplib.Geometry
import com.nextgis.maplib.Object
import com.nextgis.nextgismobile.BR
import com.nextgis.nextgismobile.R

class VectorLayer(id: Int, handle: com.nextgis.maplib.Layer?) :
    Layer(id, handle) {
    var geometryType = (handle?.dataSource as? FeatureClass)?.geometryType ?: Geometry.Type.NONE

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

    // TODO
    var notEditable = getProperty("user", "editable")?.equals("true") ?: false
        set(value) {
            field = value
            handle?.dataSource?.setProperty("editable", if (value) "true" else "false", "user")
        }

    // TODO
    @get:Bindable
    var sticking = getProperty("user", "sticking")?.equals("true") ?: true
        set(value) {
            field = value
            handle?.dataSource?.setProperty("sticking", if (value) "true" else "false", "user")
            notifyPropertyChanged(BR.sticking)
        }

    // TODO
    var stickingMode = getProperty("user", "stickingMode") ?: "3"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("stickingMode", value, "user")
        }

    // TODO
    var stickingThreshold = getProperty("user", "stickingThreshold") ?: "10"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("stickingThreshold", value, "user")
        }

    // TODO
    var stickingUnits = getProperty("user", "stickingUnits") ?: "px"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("stickingUnits", value, "user")
        }

    @get:Bindable
    var stickingLayers = 0

    // TODO
    @get:Bindable
    var caption = getProperty("user", "caption")?.equals("true") ?: true
        set(value) {
            field = value
            handle?.dataSource?.setProperty("caption", if (value) "true" else "false", "user")
            notifyPropertyChanged(BR.caption)
        }

    // TODO
    var clustering = getProperty("user", "clustering")?.equals("true") ?: true
        set(value) {
            field = value
            handle?.dataSource?.setProperty("clustering", if (value) "true" else "false", "user")
        }

    // TODO
    var fillColor = getProperty("user", "fillColor") ?: "#03A9F4"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("fillColor", value.replace("#", ""), "user")
        }

    // TODO
    var width = getProperty("user", "width") ?: "16"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("width", value, "user")
        }

    // TODO
    var strokeWidth = getProperty("user", "strokeWidth") ?: "4"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("strokeWidth", value, "user")
        }

    // TODO
    var strokeColor = getProperty("user", "strokeColor") ?: "#03A9F4"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("strokeColor", value.replace("#", ""), "user")
        }

    // TODO
    var styleMode = getProperty("user", "style") ?: "simple"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("style", value, "user")
        }

    // TODO
    var sourceField = getProperty("user", "sourceField") ?: "id"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("sourceField", value, "user")
        }

    // TODO
    var font = getProperty("user", "font") ?: "roboto"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("font", value, "user")
        }

    // TODO
    var fontSize = getProperty("user", "style") ?: "simple"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("style", value, "user")
        }

    // TODO
    var fontColor = getProperty("user", "fontColor") ?: "#03A9F4"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("fontColor", value.replace("#", ""), "user")
        }

    // TODO
    var fontStrokeColor = getProperty("user", "fontStrokeColor") ?: "#03A9F4"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("fontStrokeColor", value.replace("#", ""), "user")
        }

    // TODO
    var alignment = getProperty("user", "alignment") ?: "center"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("alignment", value, "user")
        }

    // TODO
    var figure = getProperty("user", "figure") ?: "circle"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("figure", value, "user")
        }

    // TODO
    var figureSize = getProperty("user", "figureSize") ?: "16"
        set(value) {
            field = value
            handle?.dataSource?.setProperty("figureSize", value, "user")
        }

}