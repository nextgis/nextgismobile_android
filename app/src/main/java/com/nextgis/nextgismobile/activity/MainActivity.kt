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
import android.support.design.widget.Snackbar
import android.view.Menu
import android.view.MenuItem
import com.nextgis.maplib.*
import com.nextgis.nextgismobile.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity(), GestureDelegate {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        API.init(this@MainActivity)
        val map = API.getMap("main")
        map?.let {
            it.setExtentLimits(
                MIN_X,
                MIN_Y,
                MAX_X,
                MAX_Y
            )
            mapView.setMap(it)
            addOSMTo(it)
//            it.save()
        }
        mapView.registerGestureRecognizers(this)
        mapView.freeze = false

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun addOSMTo(map: MapDocument) {
        val dataDir = API.getDataDirectory()
        if(dataDir != null) {
            val bbox = Envelope(
                MIN_X,
                MAX_X,
                MIN_Y,
                MAX_Y
            )
            val baseMap = dataDir.createTMS("osm.wconn",
                OSM_URL, 3857, 0, 18, bbox, bbox, 14)
            map.addLayer("OSM", baseMap!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val OSM_URL = "http://tile.openstreetmap.org/{z}/{x}/{y}.png"
        const val MAX_X = 20037508.34
        const val MIN_X = -MAX_X
        const val MAX_Y = 20037508.34
        const val MIN_Y = -MAX_Y
    }
}
