/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2018-2019 NextGIS, info@nextgis.com
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

import android.accounts.*
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.nextgis.maplib.util.NonNullObservableField
import com.nextgis.nextgismobile.BuildConfig
import com.nextgis.nextgismobile.data.Token
import com.nextgis.nextgismobile.data.User
import com.nextgis.nextgismobile.data.UserCreate
import com.nextgis.nextgismobile.data.Username
import com.nextgis.nextgismobile.util.AuthService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class AuthModel {
    companion object {
        public const val ACCOUNT_TYPE = BuildConfig.APPLICATION_ID + ".account"
        const val AUTHORITY = BuildConfig.APPLICATION_ID
        const val ACCOUNT = "NextGIS Mobile 3"
    }

    var accountManager: AccountManager? = null
    private val tokenType: String get() = getUserData("tokenType")

    private val authService by lazy {
        AuthService.create()
    }

    interface OnDataReadyCallback {
        fun onDataReady(data: Any?)
        fun onRequestFailed(error: String?)
    }

    fun signUp(email: String, password: String, callback: OnDataReadyCallback) {
        authService.signUp(UserCreate(email, password)).enqueue(object : Callback<Username> {
            override fun onFailure(call: Call<Username>?, t: Throwable?) {
                callback.onRequestFailed(t?.localizedMessage)
            }

            override fun onResponse(call: Call<Username>?, response: Response<Username>?) {
                response?.let {
                    if (it.isSuccessful)
                        signIn(email, password, callback)
                    else
                        callback.onRequestFailed(it.errorBody()?.string())
                }
            }
        })
    }

    fun signIn(email: String, password: String, callback: OnDataReadyCallback) {
        Log.e("NNGGWW", "signIn start")

        authService.signIn(email,password ).enqueue(object : Callback<Token> {
            override fun onFailure(call: Call<Token>?, t: Throwable?) {
                Log.e("NNGGWW", "signIn failed")
                callback.onRequestFailed(t?.localizedMessage)
            }

            override fun onResponse(call: Call<Token>?, response: Response<Token>?) {
                Log.e("NNGGWW", "signIn onResponse")
                response?.let {

                    if (it.isSuccessful) {
                        callback.onDataReady(it.body())
                        Log.e("NNGGWW", "signIn success " + response.body().toString())
                    }
                    else {
                        Log.e("NNGGWW", "signIn unsuccess " + response.body().toString())
                        callback.onRequestFailed(it.errorBody()?.string())
                    }
                }
            }
        })
    }

    fun checkUser() {
        authService.checkUser().enqueue(object  : Callback<User> {
            override fun onFailure(call: Call<User>?, t: Throwable?) {
                Log.e("NNGGWW", "checkUser failed")
//                callback.onRequestFailed(t?.localizedMessage)
            }

            override fun onResponse(call: Call<User>?, response: Response<User>?) {
                Log.e("NNGGWW", "checkUser onResponse")
                response?.let {
                    if (it.isSuccessful) {
//                        callback.onDataReady(it.body())
                        Log.e("NNGGWW", "checkUser success " + response.body().toString())
                    }
                    else {
                        Log.e("NNGGWW", "checkUser unsuccess " + response.body().toString())
//                        callback.onRequestFailed(it.errorBody()?.string())
                    }
                }
            }
        })
    }

    fun buildIntent(account: String, token: String): Intent {
        val intent = Intent()
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account)
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, ACCOUNT_TYPE)
        intent.putExtra(AccountManager.KEY_AUTHTOKEN, token)
        return intent
    }

    fun getToken(type: String, callback: AccountManagerCallback<Bundle>, isTokenGetting : NonNullObservableField<Boolean>,
                 isLoginPanelShow : NonNullObservableField<Boolean>, isLoading : NonNullObservableField<Boolean>) {
        isTokenGetting.set(true)

        val account = getAccount()
        if (account != null) {
            try {
                accountManager?.getAuthToken(account, type, null, false, callback, null)
            } catch (ignored: OperationCanceledException) {
                Log.e("app", "exception")

            } catch (ignored: IOException) {
                Log.e("app", "exception")

            } catch (ignored: AuthenticatorException) {
                Log.e("app", "exception")

            } catch (ignored: Exception){
                Log.e("app", "exception")
            }
        } else {
            isTokenGetting.set(false)
            isLoginPanelShow.set(true)
            isLoading.set(false)

        }
    }

    fun addAccount(accountName: String, login: String, token: Token): Boolean {
        var result = false
        accountManager?.let {
            var account = getAccount()
            if (account == null) {
                account = Account(accountName, ACCOUNT_TYPE)
                result = it.addAccountExplicitly(account, null, null)
            } else
                result = true
            it.setAuthToken(account, token.tokenType, token.accessToken)
            it.setAuthToken(account, "refreshToken", token.refreshToken)

            if (result) {
                setUserData("tokenType", token.tokenType)
                setUserData("expiresIn", token.expiresIn)
                setUserData("login", login)
            }
        }

        return result
    }

    private fun getAccount(): Account? {
        accountManager?.let {
            val accounts = it.getAccountsByType(ACCOUNT_TYPE)
            return if (accounts.isNotEmpty()) accounts[0] else null
        }
        return null
    }

    @Suppress("DEPRECATION")
    fun deleteAccount() {
        getAccount()?.let {
            accountManager?.removeAccount(it, null, Handler())
        }
    }

    private fun setUserData(key: String, value: String) {
        getAccount()?.let {
            accountManager?.setUserData(it, key, value)
        }
    }

    fun getUserData(key: String): String {
        getAccount()?.let {
            return accountManager?.getUserData(it, key) ?: ""
        }
        return ""
    }
}