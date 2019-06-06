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

class RasterLayer(id: Int, handle: com.nextgis.maplib.Layer?) :
    Layer(id, handle) {

    var opacity = handle?.style?.getInteger("transparency", 0) ?: 0
        set(value) {
            field = value
            handle?.style?.setInteger("transparency", value)
        }

    // TODO
    var contrast = handle?.style?.getInteger("contrast", 0) ?: 0
        set(value) {
            field = value
            handle?.style?.setInteger("contrast", value)
        }

    // TODO
    var brightness = handle?.style?.getInteger("brightness", 0) ?: 0
        set(value) {
            field = value
            handle?.style?.setInteger("brightness", value)
        }

    // TODO
    var grayscale = handle?.style?.getBool("grayscale", false) ?: false
        set(value) {
            field = value
            handle?.style?.setBoolean("grayscale", value)
        }

    var lifetime = "604800"
        get() {
            return getProperty("user", "TMS_CACHE_EXPIRES") ?: "604800"
        }
        set(value) {
            field = value
            setProperty("TMS_CACHE_EXPIRES", value, "user")
        }

    var maxCacheSize = "32"
        get() {
            val bytes = getProperty("user", "TMS_CACHE_MAX_SIZE") ?: "33554432"
            val megabytes = (bytes.toIntOrNull() ?: 33554432) / 1024 / 1024
            return megabytes.toString()
        }
        set(value) {
            field = value
            var megabytes = value.toIntOrNull() ?: 10
            megabytes = if (megabytes < 10) 10 else megabytes
            megabytes = if (megabytes > 200) 200 else megabytes
            megabytes *= 1024 * 1024
            setProperty("TMS_CACHE_MAX_SIZE", megabytes.toString(), "user")
        }

}