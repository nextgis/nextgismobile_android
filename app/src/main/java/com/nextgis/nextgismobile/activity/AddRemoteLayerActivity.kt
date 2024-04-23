/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2019-2020 NextGIS, info@nextgis.com
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
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import com.nextgis.maplib.API
import com.nextgis.maplib.ConnectionDescription
import com.nextgis.maplib.Instance
import com.nextgis.maplib.NGWConnectionDescription
import com.nextgis.maplib.Object
import com.nextgis.maplib.activity.PickerActivity
import com.nextgis.maplib.fragment.FilePickerFragment
import com.nextgis.maplib.util.NonNullObservableField
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.ActivityAddRemoteLayerBinding
import com.nextgis.nextgismobile.databinding.ActivityMainBinding
import com.nextgis.nextgismobile.fragment.DownloadVectorLayerFragment
import com.nextgis.nextgismobile.util.statusBarHeight
import com.nextgis.nextgismobile.viewmodel.LayerDownloadViewModel
import com.pawegio.kandroid.toast



class AddRemoteLayerActivity : BaseActivity(), PickerActivity {
    private lateinit var binding: ActivityAddRemoteLayerBinding
    val instance = NonNullObservableField(
        Instance("source.nextgis.com", "", "administrator", "6A5tAj0u", ""))
    private var name = ""

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRemoteLayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        name = intent.getStringExtra("instance") ?: ""

        val params = binding.root.layoutParams as FrameLayout.LayoutParams
        params.topMargin = statusBarHeight

        val downloadModel = ViewModelProvider(this).get(LayerDownloadViewModel::class.java)

        binding.apply {
            activity = this@AddRemoteLayerActivity
            model = downloadModel
        }

        binding.executePendingBindings()

        val picker = FilePickerFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, picker).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun root(): List<Object> {
        val children = listOf<Object>()
        API.getCatalog()?.children()?.let {
            for (child in it)
                if (child.type == 72) {
                    for (connection in child.children())
                        if (connection.name.startsWith(name))
                            return connection.children().toList()
                }
            return arrayListOf()
        }
        return children
    }

    override fun onLayerSelected(file: Object?) {
        toast(R.string.not_implemented)
        // todo remove
    }

    fun previous() {
        toast(R.string.not_implemented)
    }

    fun next() {
        binding.model?.next()

        // TODO handle different types
        val vector = DownloadVectorLayerFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, vector).commit()

        title = binding.model?.vectorLayer?.title
    }
}