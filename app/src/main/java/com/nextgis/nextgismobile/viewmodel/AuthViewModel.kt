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
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nextgis.maplib.Account
import com.nextgis.maplib.util.NonNullObservableField
import com.nextgis.nextgismobile.data.Token
import com.nextgis.nextgismobile.data.UserCreate
import com.nextgis.nextgismobile.model.AuthModel
import com.nextgis.nextgismobile.util.APIService
import java.util.regex.Pattern


class AuthViewModel : ViewModel() {
    companion object {
        const val EMAIL_PATTERN =
            "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    }

    private var authModel: AuthModel = AuthModel()

//    val loggedIn = NonNullObservableField(false)

    val firstTime = NonNullObservableField(true)
    val isTokenGetting = NonNullObservableField(false)

    val isLoading = NonNullObservableField(false)
    val signIn = NonNullObservableField(false)
    var login = NonNullObservableField("")
    var password = NonNullObservableField("")
    var token = MutableLiveData<Token>()
    var error = MutableLiveData<String>()
    val passwordVisible = NonNullObservableField(false)
    val account: ObservableField<Account> = ObservableField()
    val fullName = NonNullObservableField("")

    fun getVisible():Int {
        return if (isLoading.get()) View.VISIBLE else View.GONE
    }

    fun getInVisible():Int {
        return if (isLoading.get()) View.GONE else View.VISIBLE
    }
    fun init(accountManager: AccountManager?, load: Boolean) {
        //fun init(preferences: SharedPreferences, accountManager: AccountManager?, load: Boolean) {

//        loggedIn.set(preferences.getBoolean( LOGGED_IN, false))


        authModel.accountManager = accountManager
        if (!load)
            return

        val type = authModel.getUserData("tokenType")
        val expiresIn = authModel.getUserData("expiresIn")
        token.value = Token("", "", type, expiresIn)
        isTokenGetting.set(true)
        Log.e("TTOOKK","true")
        authModel.getToken(type, AccountManagerCallback { callback ->
            val token = callback.result.getString(AccountManager.KEY_AUTHTOKEN, "")
            this.token.value?.accessToken = token
            APIService.TOKEN = "$type $token"
            this.token.value?.let {
                if (it.refreshToken.isNotBlank())
                    addAuth(it)
            }
        }, isTokenGetting)
        authModel.getToken("refreshToken", AccountManagerCallback { callback ->
            val token = callback.result.getString(AccountManager.KEY_AUTHTOKEN, "")
            this.token.value?.refreshToken = token
            this.token.value?.let {
                if (it.accessToken.isNotBlank())
                    addAuth(it)
                isTokenGetting.set(false)
                Log.e("TTOOKK","false")
            }
        }, isTokenGetting)
    }

    fun checkUser(){
        if (this.token.value != null) {
            authModel.checkUser()
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
        error.value = message
    }

    fun signIn() {
        isLoading.set(true)
        authModel.signIn(login.get(), password.get(), object : AuthModel.OnDataReadyCallback {
            override fun onDataReady(data: Any?) {
                isLoading.set(false)
                token.value = data as Token?
                token.value?.let {
                    APIService.TOKEN = "${it.tokenType} ${it.accessToken}"
                    addAuth(it)
                    APIService.TOKEN = "${it.tokenType} ${it.accessToken}"
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
                    addAuth(it)
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

        val account = Account(UserCreate.client_id_old, token.accessToken, token.refreshToken,
            authorizeFailedCallback = {
                Log.e("NNGGWW", "authorizeFailedCallback"  )
            })

        Log.e("NNGGWW", "account created with token " + token.accessToken + " refresh " + token.refreshToken)
        Log.e("NNGGWW", "emailis " + account.email)
//        account.updateInfo()
//        Log.e("NNGGWW", "emailis after update " + account.email)
        val updateResult = account.updateSupportInfo()
        Log.e("NNGGWW", "updateSupport result " + updateResult)
        Log.e("NNGGWW", "emailis after updateSupport " + account.email)
        account.updateInfo()
        Log.e("NNGGWW", "emailis after update " + account.email)
        fullName.set((account.firstName + " " + account.lastName).trim())
        login.set(account.email)
        this.account.set(account)

        val options = account.options()
        if (options == null)
            Log.e("nul","ff")


        // updateInfoFromServer()
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
    }
}