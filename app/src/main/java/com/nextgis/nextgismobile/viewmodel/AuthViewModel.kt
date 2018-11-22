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

import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.databinding.ObservableField
import com.nextgis.nextgismobile.data.Token
import com.nextgis.nextgismobile.data.User
import com.nextgis.nextgismobile.model.AuthModel
import com.nextgis.nextgismobile.util.APIService
import com.nextgis.nextgismobile.util.NonNullObservableField
import java.util.regex.Pattern

class AuthViewModel : ViewModel() {
    companion object {
        const val EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    }

    private var authModel: AuthModel = AuthModel()
    val firstTime = NonNullObservableField(true)
    val isLoading = NonNullObservableField(false)
    val signIn = NonNullObservableField(false)
    var login = NonNullObservableField("")
    var password = NonNullObservableField("")
    var user = ObservableField<User>()
    var token = MutableLiveData<Token>()
    var error = MutableLiveData<String>()
    val isAuthorized = login.get().isNotBlank()
    val passwordVisible = NonNullObservableField(false)

    fun init(accountManager: AccountManager?, load: Boolean) {
        authModel.accountManager = accountManager
        if (!load)
            return

        authModel.getToken(AccountManagerCallback {
            val token = it.result.getString(AccountManager.KEY_AUTHTOKEN, "")
            val type = authModel.getUserData("token_type")
            this.token.value = Token(token, "", type) // TODO refresh
            APIService.TOKEN = "$type $token"
        })
        login.set(authModel.getUserData("login"))
    }

    fun buildIntent(account: String, token: String): Intent {
        return authModel.buildIntent(account, token)
    }

//    fun profile() {
//        authModel.profile(object : AuthModel.OnDataReadyCallback {
//            override fun onDataReady(data: Any?) {
//                user.set(data as User?)
//            }
//
//            override fun onRequestFailed(error: String?) {
//                onFailure(error)
//            }
//        })
//    }

    fun isEmailValid(): Boolean {
        val pattern = Pattern.compile(EMAIL_PATTERN)
        val matcher = pattern.matcher(login.get().trim())
        return matcher.matches()
    }

    fun isPasswordEmpty(): Boolean {
        return password.get().isBlank() ?: false
    }

    private fun onFailure(message: String?) {
        isLoading.set(false)
        error.value = message
    }

    fun signIn() {
        isLoading.set(true)
        authModel.signIn(login.get(), password.get(), object : AuthModel.OnDataReadyCallback {
            override fun onDataReady(data: Any?) {
                isLoading.set(false)
                token.value = data as Token?
            }

            override fun onRequestFailed(error: String?) {
                onFailure(error)
            }
        })
    }

    fun signUp() {
//        isLoading.set(true)
//        authModel.signUp(login.get(), password.get(), object : AuthModel.OnDataReadyCallback {
//            override fun onDataReady(data: Any?) {
//                isLoading.set(false)
//                token.value = data as Token?
//            }
//
//            override fun onRequestFailed(error: String?) {
//                onFailure(error)
//            }
//        })
    }

    fun addAccount(account: String, token: Token): Boolean {
        return authModel.addAccount(account, login.get(), token)
    }

    fun deleteAccount() {
        authModel.deleteAccount()
    }
}