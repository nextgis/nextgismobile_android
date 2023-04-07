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

import android.os.Bundle
 import androidx.preference.PreferenceManager
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.ActivityProxyBinding
import com.pawegio.kandroid.runDelayed
import com.pawegio.kandroid.startActivity


class ProxyActivity : BaseActivity() {
    private lateinit var binding: ActivityProxyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProxyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //DataBindingUtil.setContentView(this, R.layout.activity_proxy)
        binding.executePendingBindings()
        hideStatusBar(window)

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false)
        val intro = !preferences.getBoolean("intro_shown", false)
        val signin = !preferences.getBoolean("ngid_shown", false)
        runDelayed(2500) {
            when {
                intro -> startActivity<IntroActivity>()
                signin -> startActivity<NGIDSigninActivity>()
                else -> startActivity<MainActivity>()
            }
        }
    }
}
