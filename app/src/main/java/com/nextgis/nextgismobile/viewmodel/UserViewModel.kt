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

package com.nextgis.nextgismobile.viewmodel

import androidx.lifecycle.ViewModel
import com.nextgis.nextgismobile.data.User
import com.nextgis.nextgismobile.model.UserModel
import com.nextgis.nextgismobile.util.NonNullObservableField

class UserViewModel : ViewModel() {
    private var userModel: UserModel = UserModel()
    var fullName = NonNullObservableField("")
    var email = NonNullObservableField("")
    var avatar = NonNullObservableField("")
    val isSupported = NonNullObservableField(false)

    fun profile() {
        userModel.profile(object : UserModel.OnDataReadyCallback {
            override fun onError(profile: String?) {

            }

            override fun onDataReady(profile: User?) {
                profile?.let {
                    fullName.set(formatFullName(it))
                    email.set(it.email)
                    avatar.set(it.avatar)
                }
            }
        })
    }

    private fun formatFullName(user: User): String {
        var fullName = ""
        if (user.first_name.isNotBlank())
            fullName += user.first_name + " "
        if (user.last_name.isNotBlank())
            fullName += user.last_name + " "
        if (user.username.isNotBlank()) {
            fullName += if (fullName.isNotBlank()) {
                "(" + user.username + ")"
            } else
                user.username
        }
        return fullName
    }
}