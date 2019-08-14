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

import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.ActivitySettingsBinding
import com.nextgis.nextgismobile.fragment.settings.HeadersFragment
import com.nextgis.nextgismobile.viewmodel.AuthViewModel
import com.nextgis.nextgismobile.viewmodel.SettingsViewModel
import com.pawegio.kandroid.accountManager
import com.pawegio.kandroid.startActivity
import kotlinx.android.synthetic.main.activity_settings.*


class SettingsActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val settingsModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        settingsModel.setup(this)
        settingsModel.load()

        val authModel = ViewModelProviders.of(this).get(AuthViewModel::class.java)
        binding.auth = authModel
        val headers = HeadersFragment()
        supportFragmentManager.beginTransaction().replace(R.id.headers, headers).addToBackStack("headers").commitAllowingStateLoss()
        binding.executePendingBindings()
        authModel.init(accountManager, true)
    }

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
        val settingsModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        settingsModel.save()
    }

    override fun onBackPressed() {
        handleBackPress()
    }

    private fun handleBackPress() {
        if (supportFragmentManager.backStackEntryCount <= 2)
            supportActionBar?.setTitle(R.string.action_settings)

        if (supportFragmentManager.backStackEntryCount > 1)
            supportFragmentManager.popBackStack()
        else
            finish()
    }
}