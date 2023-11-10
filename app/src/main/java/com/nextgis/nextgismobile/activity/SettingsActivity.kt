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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.ActivitySettingsBinding
import com.nextgis.nextgismobile.fragment.settings.HeadersFragment
//import com.nextgis.nextgismobile.model.SettingsModel.Companion.PREF
import com.nextgis.nextgismobile.util.statusBarHeight
import com.nextgis.nextgismobile.viewmodel.AuthViewModel
import com.nextgis.nextgismobile.viewmodel.SettingsViewModel
import com.pawegio.kandroid.accountManager


class SettingsActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val params = binding.root.layoutParams as FrameLayout.LayoutParams
        params.topMargin = statusBarHeight

        val settingsModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        settingsModel.setup(this)
        settingsModel.load()

        val authModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        binding.auth = authModel
        val headers = HeadersFragment()
        supportFragmentManager.beginTransaction().replace(R.id.headers, headers).addToBackStack("headers").commitAllowingStateLoss()
        binding.executePendingBindings()

        authModel.init(accountManager, true)
//        authModel.init(getSharedPreferences(PREF, Context.MODE_MULTI_PROCESS), accountManager, true)


//        val runnable: Runnable = object : Runnable {
//            override fun run() {
//                // Переносим сюда старый код
//                //authModel.checkUser()
//            }
//        }
//        val thread = Thread(runnable)
//        thread.start()
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        val authModel = ViewModelProvider(this).get(AuthViewModel::class.java)
//        authModel.init(getSharedPreferences(PREF, Context.MODE_MULTI_PROCESS), accountManager, true)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                handleBackPress()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        val settingsModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        settingsModel.save()
    }

    override fun onBackPressed() {
        handleBackPress()
    }

    private fun handleBackPress() {
        invalidateOptionsMenu()

        if (supportFragmentManager.backStackEntryCount <= 2)
            supportActionBar?.setTitle(R.string.action_settings)

        if (supportFragmentManager.backStackEntryCount > 1)
            supportFragmentManager.popBackStack()
        else
            finish()
    }
}