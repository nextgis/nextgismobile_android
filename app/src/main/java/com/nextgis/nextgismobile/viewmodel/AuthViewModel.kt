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

package com.nextgis.nextgismobile.viewmodel

//import com.nextgis.nextgismobile.model.SettingsModel.Companion.LOGGED_IN
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.nextgis.maplib.API
import com.nextgis.maplib.Account
import com.nextgis.maplib.Constants
import com.nextgis.maplib.util.NonNullObservableField
import com.nextgis.nextgismobile.data.Token
import com.nextgis.nextgismobile.data.UserCreate
import com.nextgis.nextgismobile.model.AuthModel
import com.nextgis.nextgismobile.util.APIService
import com.pawegio.kandroid.runAsync
import com.pawegio.kandroid.runOnUiThread
import java.util.regex.Pattern


class AuthViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val EMAIL_PATTERN =
            "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    }

    private var authModel: AuthModel = AuthModel()

//    val loggedIn = NonNullObservableField(false)

    val lastError = NonNullObservableField("token update error")
    val loginWasDone = NonNullObservableField(false)
    val firstTime = NonNullObservableField(true)
    val isTokenGetting = NonNullObservableField(false)
    val isLoginPanelShow = NonNullObservableField(false) // no login was
    val isTokenUpdateError = NonNullObservableField(false) // token error - after update


    val isLoading = NonNullObservableField(false)
    val signIn = NonNullObservableField(false)
    var login = NonNullObservableField("")
    var password = NonNullObservableField("")
    var token = MutableLiveData<Token>()
    var error = MutableLiveData<String>()
    val passwordVisible = NonNullObservableField(false)
    val isExternalOnline = NonNullObservableField(true)
    val account: ObservableField<Account> = ObservableField()
    val fullName = NonNullObservableField("")
    val showBeforeUpdate = NonNullObservableField(false)


    fun init(accountManager: AccountManager?, load: Boolean) {

        Log.e("AAUUTTHHH","AuthViewModel init()")
        Log.e("AAUUTTHHH", "executing AuthViewModel init() on..." + Thread.currentThread().name)

        // load pref values stored on prev steps
        val context = getApplication<Application>().applicationContext
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val storedFullName = preferences.getString(Constants.SettingsStrings.account_fullName, "")
        val storedLogin = preferences.getString(Constants.SettingsStrings.account_login, "")
        val storedLoginWasDone = preferences.getBoolean(Constants.SettingsStrings.account_login_was_done, false)
        storedFullName?.let { fullName.set(it) }
        storedLogin?.let { login.set(storedLogin) }
        loginWasDone.set(storedLoginWasDone)
        if (!TextUtils.isEmpty(storedFullName) || !TextUtils.isEmpty(storedLogin))
            showBeforeUpdate.set(true)

        authModel.accountManager = accountManager
        if (!load)
            return

        val type = authModel.getUserData("tokenType")
        val expiresIn = authModel.getUserData("expiresIn")
        token.value = Token("", "", type, expiresIn)
        isTokenGetting.set(true)
        isLoginPanelShow.set(false)
        isLoading.set(true)
        isTokenUpdateError.set(false)
        Log.e("AAUUTTHHH","start authModel.getToken type (access)")
        authModel.getToken(type, AccountManagerCallback { callback ->
            Log.e("AAUUTTHHH","start authModel.getToken type (access) callback")
            Log.e("AAUUTTHHH", "executing getToken access on..." + Thread.currentThread().name)
            val token = callback.result.getString(AccountManager.KEY_AUTHTOKEN, "")
            this.token.value?.accessToken = token
            APIService.TOKEN = "$type $token"
            this.token.value?.let {
                if (it.refreshToken.isNotBlank()) {
                    runAsync {
                        Log.e("AAUUTTHHH", "executing runAsync on..." + Thread.currentThread().name)
                        addAuth(it)
                    }
                }
            }
        }, isTokenGetting, isLoginPanelShow, isLoading)
        Log.e("AAUUTTHHH","start authModel.getToken refreshToken")
        authModel.getToken("refreshToken", AccountManagerCallback { callback ->
            Log.e("AAUUTTHHH","start authModel.getToken refreshToken callback")
            Log.e("AAUUTTHHH", "executing getToken refreshToken  on..." + Thread.currentThread().name)
            val token = callback.result.getString(AccountManager.KEY_AUTHTOKEN, "")
            this.token.value?.refreshToken = token
            this.token.value?.let {
                if (it.accessToken.isNotBlank()) {
                    runAsync {
                        addAuth(it)
                    }
                }
                isTokenGetting.set(false)
                Log.e("TTOOKK","false")
            }
        }, isTokenGetting, isLoginPanelShow, isLoading)
    }

    fun checkUser(){
        if (this.token.value != null) {
            authModel.checkUser()
            View.VISIBLE
        }
    }

    fun buildIntent(account: String, token: String): Intent {
        return authModel.buildIntent(account, token)
    }

    fun isEmailValid(): Boolean {
        val pattern = Pattern.compile(EMAIL_PATTERN)
        val matcher = pattern.matcher(login.get().trim())
        return matcher.matches()
    }

    fun isPasswordEmpty(): Boolean {
        return password.get().isBlank()
    }

    private fun onFailure(message: String?) {
        isLoading.set(false)
        isTokenGetting.set(false)
        Log.e("TTOOKK","false")

        message?.let {
            error.value = if (message == null) "" else message
        } ?:run{
            error.value = "null error message"
        }

    }

    fun signIn() {
        isLoading.set(true)
        authModel.signIn(login.get(), password.get(), object : AuthModel.OnDataReadyCallback {
            override fun onDataReady(data: Any?) {
                isLoading.set(false)
                token.value = data as Token?
                token.value?.let {
                    APIService.TOKEN = "${it.tokenType} ${it.accessToken}"
                    runAsync {
                        addAuth(it)
                    }

                    //APIService.TOKEN = "${it.tokenType} ${it.accessToken}"
                }
            }

            override fun onRequestFailed(error: String?) {
                onFailure(error)
            }
        })
    }

    fun signUp() { // sharedPreferences: SharedPreferences
        isLoading.set(true)
        authModel.signUp(login.get(), password.get(), object : AuthModel.OnDataReadyCallback {
            override fun onDataReady(data: Any?) {
                isLoading.set(false)
                token.value = data as Token?
                token.value?.let {
                    runAsync {
                        addAuth(it)
                    }
                }
            }

            override fun onRequestFailed(error: String?) {
                onFailure(error)
            }
        })
    }

    private fun addAuth(token: Token) {
        //        val url = "http://source.nextgis.com"
        //        val authUrl = "https://my.nextgis.com"
        //        API.addAuth(Auth(url, authUrl, token.accessToken, token.refreshToken, token.expiresIn, UserCreate.client_id, {}))
        Log.e("AAUUTTHHH", "start executing addAuth  on..." + Thread.currentThread().name)
        Log.e("AAUUTTHHH","start addAuth")

        val account = Account(UserCreate.client_id_old, token.accessToken, token.refreshToken,
            authorizeFailedCallback = {
                Log.e("NNGGWW", "authorizeFailedCallback"  )
            })

        account.updateInfo()
        val updateResult = account.updateSupportInfo()

        isLoading.set(false)
        if (loginWasDone.get() && !account.authorized){
            // some error on update info - as one reason - unable refresh token
            isTokenUpdateError.set(true)
        }

        runOnUiThread {
            if (loginWasDone.get() && account.authorized == false && account.firstName.equals("") && account.lastName.equals("")
                && account.email.equals("")) {
                // looks like token update  error - show attention and not change saved names and email
                isTokenUpdateError.set(true)
                if (!isExternalOnline.get())
                    lastError.set("no internet to check authority")

            } else {
                fullName.set((account.firstName + " " + account.lastName).trim())
                login.set(account.email)
                // not update if update fail
                isTokenUpdateError.set(false)
            }

            this.account.set(account)
            val options = account.options()
            if (options == null)
                Log.e("nul", "ff")

            // store data - to show fast account info
            if (account.authorized) { //auth success - save data
                val context = getApplication<Application>().applicationContext
                val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
                preferences.edit().putString(
                    Constants.SettingsStrings.account_fullName,
                    (account.firstName + " " + account.lastName).trim()
                ).apply()
                preferences.edit().putString(Constants.SettingsStrings.account_login, account.email).apply()

                if (!loginWasDone.get()) {
                    preferences.edit().putBoolean(Constants.SettingsStrings.account_login_was_done, true).apply()
                    loginWasDone.set(true)
                }
                preferences.edit().putString(Constants.SettingsStrings.account_login, account.email).apply()
            }
        }
    }

    private fun updateInfoFromServer() {
        val updateResult = account.get()?.let {
            it.updateSupportInfo()
        }
        Log.e("NNGGWW", "updateSupport result " + updateResult)
        Log.e("NNGGWW", "emailis after updateSupport " + account.get()?.email)
        account.get()?.updateInfo()
        Log.e("NNGGWW", "emailis after update " + account.get()?.email)
        fullName.set((account.get()?.firstName + " " + account.get()?.lastName).trim())
        if (account.get() != null)
            login.set(account.get()!!.email)
    }
    fun addAccount(account: String, token: Token): Boolean {
        return authModel.addAccount(account, login.get(), token)
    }

    fun deleteAccount() {
        authModel.deleteAccount()
        account.get()?.exit()
        account.set(Account("", "", ""))
        login.set("")
        this.token.value = null
        APIService.TOKEN = ""

        val context = getApplication<Application>().applicationContext
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        preferences.edit().remove(Constants.SettingsStrings.account_fullName).apply()
        preferences.edit().remove(Constants.SettingsStrings.account_login).apply()
        preferences.edit().remove(Constants.SettingsStrings.account_login_was_done).apply()
        loginWasDone.set(false)
        isLoginPanelShow.set(true)
        isLoading.set(false)
        signIn.set(false)
        showBeforeUpdate.set(false)
    }
}