/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2018 NextGIS, info@nextgis.com
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

import android.annotation.SuppressLint
import androidx.databinding.BaseObservable
import com.google.gson.annotations.SerializedName
import java.security.MessageDigest

class User(@SerializedName("first_name") var firstName: String, @SerializedName("last_name") var lastName: String,
           var username: String, var email: String) : BaseObservable() {
    val name: String get() = "$firstName $lastName".trim()
    var avatar = ""
    val gravatar: String
        @SuppressLint("DefaultLocale")
        get() {
            val md = MessageDigest.getInstance("MD5")
            md.update(email.trim().toLowerCase().toByteArray())
            val byteData = md.digest()
            val hexString = StringBuffer()
            for (i in byteData.indices) {
                val hex = Integer.toHexString(255 and byteData[i].toInt())
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }

            val hash = hexString.toString()
            return "https://www.gravatar.com/avatar/$hash?s=120&d=404"
        }
}