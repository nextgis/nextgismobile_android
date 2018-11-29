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

package com.nextgis.nextgismobile.model

import com.nextgis.nextgismobile.data.User
import com.nextgis.nextgismobile.util.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserModel {
    private val apiService by lazy {
        APIService.create()
    }

    interface OnDataReadyCallback {
        fun onDataReady(profile: User?)
        fun onError(profile: String?)
    }

    fun profile(onDataReady: OnDataReadyCallback) {
        apiService.profile().enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>?, t: Throwable?) {
                onDataReady.onError(t?.localizedMessage)
            }

            override fun onResponse(call: Call<User>?, response: Response<User>?) {
                response?.let {
                    if (it.code() == 200)
                        onDataReady.onDataReady(it.body())
                    else
                        onDataReady.onError(it.errorBody().toString())
                }
            }
        })
    }
}