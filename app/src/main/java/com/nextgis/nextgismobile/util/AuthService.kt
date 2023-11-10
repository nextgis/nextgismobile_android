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

package com.nextgis.nextgismobile.util

import com.nextgis.nextgismobile.BuildConfig
import com.nextgis.nextgismobile.data.Token
import com.nextgis.nextgismobile.data.User
import com.nextgis.nextgismobile.data.UserAuth
import com.nextgis.nextgismobile.data.UserCreate
import com.nextgis.nextgismobile.data.Username
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface AuthService {
    companion object {
        fun create(): AuthService {
            return APIService.build(APIService.SERVER_MY).create(AuthService::class.java)
        }
    }

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("oauth2/token/")
    fun signIn(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grant_type: String = "password",
        @Field("client_id") client_id: String = BuildConfig.CLIENT_ID_OLD
    ): Call<Token>

    @POST("oauth2/token/")
    fun signIn(@Body credentials: UserAuth): Call<Token>

    @Headers("connection:keep-alive")
    @POST("oauth2/token/")
    fun signIn(@Body prompt: String): Call<Token>

    @POST("api/v1/integration/user_create/")
    fun signUp(@Body credentials: UserCreate): Call<Username>

    @Headers("connection:keep-alive")
    @GET("api/v1/user_info/")
    fun checkUser(): Call<User>



}
