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

package com.nextgis.nextgismobile.activity

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AlertDialog
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_signing -> {
                binding.auth?.let {
                    if (it.isAuthorized.get())
                        showConfirmation(it)
                    else
                        startActivity<NGIDSigninActivity>()
                }
                true
            }
            android.R.id.home -> {
                handleBackPress()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showConfirmation(auth: AuthViewModel) {
        val builder = AlertDialog.Builder(this)
            .setTitle(R.string.confirmation)
            .setMessage(R.string.ngid_disconnect)
            .setPositiveButton(android.R.string.ok) { _, _ -> auth.deleteAccount() }
            .setNegativeButton(android.R.string.cancel, null).create()
        builder.show()
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
        if (supportFragmentManager.backStackEntryCount == 1)
            supportActionBar?.setTitle(R.string.action_settings)
        if (supportFragmentManager.backStackEntryCount > 1)
            supportFragmentManager.popBackStack()
        else
            finish()
    }
}