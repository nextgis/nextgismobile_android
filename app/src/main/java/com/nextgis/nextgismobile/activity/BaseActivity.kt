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

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
 import androidx.preference.PreferenceManager


import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.nextgis.maplib.Object
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.viewmodel.MapViewModel
import com.pawegio.kandroid.toast

abstract class BaseActivity : AppCompatActivity() {
    protected lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    companion object {
        fun hideStatusBar(window: Window) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    protected fun addLayer(title: String, layer: Object) {
        val mapModel = ViewModelProvider(this).get(MapViewModel::class.java)
        val map = mapModel.load(this)
        map?.addLayer(title, layer)
        map?.save()?.let { success ->
            if (success) {
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                toast(R.string.add_layer_failed)
            }
        }
    }
}
