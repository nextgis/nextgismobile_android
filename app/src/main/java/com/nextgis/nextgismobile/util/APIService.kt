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

import android.os.Build
import com.nextgis.nextgismobile.BuildConfig
import com.nextgis.nextgismobile.data.User
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.IOException
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

interface APIService {
    companion object {
        private const val JSON = "application/json;charset=UTF-8;version=0"
        private const val WEB_SCHEME = "https"
        private const val SCHEME = "$WEB_SCHEME://"
        private const val DOMAIN = "nextgis.com"
        internal const val SERVER_MY = SCHEME + "my." + DOMAIN + "/"
        private const val SERVER_API = SERVER_MY + "api/v1/"

        var TOKEN = ""
        private val userAgent = String.format("NextGIS Mobile %s r ev. %d (Android %s; API %d)",
                BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE, Build.VERSION.RELEASE, Build.VERSION.SDK_INT)

        fun build(base: String = SERVER_API): Retrofit {
            // create TrustManager without check cert
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                    // no need to check client cert
                }

                override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                    // no need to check server cert
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
            })



            // create SSL context and turning  TrustManager
            val sslContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())

            // get  SSLSocketFactory from  SSL context
            val sslSocketFactory: SSLSocketFactory = sslContext.socketFactory

            // create  OkHttpClient with complete  SSLSocketFactory
            val client: OkHttpClient = OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .addNetworkInterceptor(CustomInterceptor())
                .hostnameVerifier { _, _ -> true } // allow skip  checking hostname
                .build()

            // create  Retrofit with using  OkHttpClient
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(base)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit

            // use Retrofit to create  API-service
            //            val apiService: YourApiService = retrofit.create(YourApiService::class.java)
            //            return Retrofit.Builder()
            //                    .client(OkHttpClient.Builder().
            //                    addNetworkInterceptor(CustomInterceptor()).build())
            //                    .addConverterFactory(GsonConverterFactory.create())
            //                    .baseUrl(base)
            //                    .build()
        }

        fun create(): APIService {
            return build().create(APIService::class.java)
        }

        class CustomInterceptor : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val builder = chain.request().newBuilder()
                builder.addHeader("Accept-Language", Locale.getDefault().language)
                builder.removeHeader("User-Agent")
                builder.addHeader("User-Agent", userAgent)
                if (TOKEN.isNotBlank())
                    builder.addHeader("Authorization", TOKEN)
                builder.addHeader("Accept", JSON)
                val request = builder.build()
                return chain.proceed(request)
//                var response = chain.proceed(request)
//
//                response.body()?.let {
//                    try {
//                        val data = it.string()
//                        response = response.newBuilder().body(ResponseBody.create(it.contentType(), data)).build()
//                    } catch (e: JSONException) {}
//                }
//                return response
            }
        }
    }

    @GET("user_info/")
    fun profile(): Call<User>
}