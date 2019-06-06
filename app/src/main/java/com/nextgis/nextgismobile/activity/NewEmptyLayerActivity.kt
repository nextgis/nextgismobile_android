/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2019 NextGIS, info@nextgis.com
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

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.nextgis.maplib.API
import com.nextgis.maplib.Field
import com.nextgis.maplib.Geometry
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.adapter.DropdownAdapter
import com.nextgis.nextgismobile.data.VectorLayer
import com.nextgis.nextgismobile.databinding.ActivityNewLayerBinding
import com.nextgis.nextgismobile.util.setup
import com.nextgis.nextgismobile.util.tint
import com.nextgis.nextgismobile.viewmodel.MapViewModel
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.activity_new_layer.*

class NewEmptyLayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewLayerBinding
    private val vectorLayer = VectorLayer(0, null)

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_layer)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

//        val settingsModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
//        settingsModel.setup(this)
//        settingsModel.load()

        binding.apply {
            layer = vectorLayer
            activity = this@NewEmptyLayerActivity

            val entries = resources.getStringArray(R.array.geometry_type)
            val values = resources.getStringArray(R.array.geometry_type_value)
            val adapter = DropdownAdapter(this@NewEmptyLayerActivity, R.layout.item_dropdown, entries)
            type.setAdapter(adapter)

            type.setText(entries[0])
            vectorLayer.geometryType = Geometry.Type.POINT
            type.setOnItemClickListener { _, _, position, _ -> vectorLayer.geometryType = Geometry.Type.from(values[position].toInt()) }
            type.setup()

            fab.tint(R.color.white)
        }

        binding.executePendingBindings()
    }

    fun save() {
        API.getStore()?.let {
            val options = mapOf(
                "CREATE_OVERVIEWS" to "ON",
                "ZOOM_LEVELS" to "2,3,4,5,6,7,8,9,10,11,12,13,14"
            )

            val fields = listOf(
                Field("long", "long", Field.Type.REAL),
                Field("lat", "lat", Field.Type.REAL),
                Field("datetime", "datetime", Field.Type.DATE, "CURRENT_TIMESTAMP"),
                Field("name", "name", Field.Type.STRING)
            )

            val newLayer = it.createFeatureClass(vectorLayer.title, vectorLayer.geometryType, fields, options)
            if (newLayer != null) {
                val mapModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
                val map = mapModel.load()
                map?.addLayer(vectorLayer.title, newLayer)
                map?.save()?.let { success ->
                    if (success) {
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        toast(R.string.not_implemented)
                    }
                }
            } else {
                toast(R.string.not_implemented)
            }
        }
    }

    fun addField() {
        toast(R.string.not_implemented)
    }
}