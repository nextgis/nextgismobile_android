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

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import com.nextgis.maplib.*
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.ActivityMainBinding
import com.pawegio.kandroid.startActivity
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import com.nextgis.nextgismobile.fragment.LayersFragment
import com.nextgis.nextgismobile.util.tint
import com.nextgis.nextgismobile.viewmodel.MapViewModel


class MainActivity : BaseActivity(), GestureDelegate {
    private lateinit var binding: ActivityMainBinding
    internal var map: MapDocument? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(bottomBar)

        initMap()
        binding.activity = this
        binding.executePendingBindings()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        getWindow().setStatusBarColor(getColor(R.color.whiteAlpha));
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        fab.tint(R.color.colorButton)
        fab.setOnClickListener {
            Snackbar.make(coordinator, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).apply {
                    view.layoutParams = (view.layoutParams as CoordinatorLayout.LayoutParams).apply {
                        setMargins(leftMargin, topMargin, rightMargin, bottomBar.height + fab.height / 4 * 3)
                    }
                }.show()
        }
    }

    private fun initMap() {
        val mapModel = ViewModelProviders.of(this).get(MapViewModel::class.java)
        mapModel.init(this)

        map = mapModel.load()
        map?.let { mapView.setMap(it) }
        mapView.registerGestureRecognizers(this)
        mapView.freeze = false
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity<SettingsActivity>()
                true
            }
            R.id.action_null -> {
                toast(R.string.not_implemented)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun layers() {
        val layers = LayersFragment()
        supportFragmentManager.beginTransaction().replace(R.id.coordinator, layers).addToBackStack("layers").commitAllowingStateLoss()
    }

    fun search() {
        toast(R.string.not_implemented)
    }

    fun refresh() {
//        mapView.refresh()
        mapView.invalidate(mapView.mapExtent)
    }
}
