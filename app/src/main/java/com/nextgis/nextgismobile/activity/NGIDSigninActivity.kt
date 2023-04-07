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

package com.nextgis.nextgismobile.activity

import android.app.Activity
import androidx.lifecycle.Observer
 
import android.content.SharedPreferences
import androidx.databinding.Observable
import android.os.Bundle
 import androidx.preference.PreferenceManager
import androidx.appcompat.app.AlertDialog
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.auth.AccountAuthenticatorActivity
import com.nextgis.nextgismobile.databinding.ActivitySigninBinding
import com.nextgis.nextgismobile.fragment.SigninFragment
import com.nextgis.nextgismobile.fragment.SignupFragment
import com.nextgis.nextgismobile.viewmodel.AuthViewModel
import com.pawegio.kandroid.accountManager
import com.pawegio.kandroid.startActivity
import com.pawegio.kandroid.toast


class NGIDSigninActivity: AccountAuthenticatorActivity() {
    private lateinit var binding: ActivitySigninBinding
    private val preferences: SharedPreferences get() = PreferenceManager.getDefaultSharedPreferences(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseActivity.hideStatusBar(window)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //DataBindingUtil.setContentView(this, R.layout.activity_signin)
        val authModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        binding.auth = authModel
        authModel.init(accountManager, false)
        authModel.firstTime.set(!preferences.getBoolean("ngid_shown", false))

        authModel.account.get()?.let {
            if (it.authorized) {
                val newAccount = intent.getBooleanExtra("add_account", false)
                showInfo(newAccount)
                return
            }
        }

        authModel.signIn.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val signIn = authModel.signIn.get()
                val tag = if (signIn) "FRAGMENT_NGID_SIGNIN" else "FRAGMENT_NGID_SIGNUP"
                val fragment = supportFragmentManager.findFragmentByTag(tag) ?: if (signIn) SigninFragment() else SignupFragment()
                supportFragmentManager.beginTransaction().replace(R.id.method, fragment, tag).commitAllowingStateLoss()
            }
        })
        authModel.token.observe(this, Observer { token ->
            token?.let {
                val account = getString(R.string.app_name)
                val intent = authModel.buildIntent(account, it.accessToken)
                val added = authModel.addAccount(account, it)
                if (added) {
                    setAccountAuthenticatorResult(intent.extras)
                    setResult(Activity.RESULT_OK, intent)
                    startActivity<MainActivity>() // TODO diff targets
                    finish()
                } // TODO error
            }
        })
        authModel.error.observe(this, Observer {
            toast(authModel.error.value.toString())
        })
        signIn(binding.root)
        binding.executePendingBindings()
        preferences.edit().putBoolean("ngid_shown", true).apply()
    }

    @Suppress("UNUSED_PARAMETER")
    fun skip(view: View) {
        startActivity<MainActivity>()
        finish()
    }

    @Suppress("UNUSED_PARAMETER")
    fun signIn(view: View) {
        binding.auth?.signIn?.set(true)
    }

    @Suppress("UNUSED_PARAMETER")
    fun signUp(view: View) {
        binding.auth?.signIn?.set(false)
    }

    private fun showInfo(newAccount: Boolean) {
        if (newAccount)
            showAlertDialog()
        else {
            toast(R.string.signed_in_already)
            finish()
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.signed_in_already)
            .setMessage(R.string.sign_out_first)
            .setPositiveButton(android.R.string.ok, null)
            .setOnDismissListener { finish() }.create()
        builder.show()
    }
}