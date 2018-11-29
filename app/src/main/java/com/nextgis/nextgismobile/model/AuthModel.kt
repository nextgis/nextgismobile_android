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

import android.accounts.*
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.nextgis.nextgismobile.BuildConfig
import com.nextgis.nextgismobile.data.Token
import com.nextgis.nextgismobile.data.UserAuth
import com.nextgis.nextgismobile.data.UserCreate
import com.nextgis.nextgismobile.data.Username
import com.nextgis.nextgismobile.util.AuthService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class AuthModel {
    companion object {
        private const val ACCOUNT_TYPE = BuildConfig.APPLICATION_ID + ".account"
    }

    var accountManager: AccountManager? = null
    private val tokenType: String get() = getUserData("token_type")

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
//        authService.signIn(UserAuth(email, password)).enqueue(object : Callback<Token> {
        authService.signIn(email, password).enqueue(object : Callback<Token> {
            override fun onFailure(call: Call<Token>?, t: Throwable?) {
                callback.onRequestFailed(t?.localizedMessage)
            }

            override fun onResponse(call: Call<Token>?, response: Response<Token>?) {
                response?.let {
                    if (it.isSuccessful)
                        callback.onDataReady(it.body())
                    else
                        callback.onRequestFailed(it.errorBody()?.string())
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

    fun getToken(callback: AccountManagerCallback<Bundle>) {
        val account = getAccount()
        if (account != null) {
            try {
                accountManager?.getAuthToken(account, tokenType, null, false, callback, null)
            } catch (ignored: OperationCanceledException) {
            } catch (ignored: IOException) {
            } catch (ignored: AuthenticatorException) {
            }
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
            it.setAuthToken(account, token.token_type, token.access_token)
            it.setAuthToken(account, "refresh_token", token.refresh_token)

            if (result) {
                setUserData("token_type", token.token_type)
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