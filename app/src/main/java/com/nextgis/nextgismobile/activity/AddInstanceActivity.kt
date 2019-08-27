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
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.nextgis.maplib.API
import com.nextgis.maplib.NGWConnection
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.data.Instance
import com.nextgis.nextgismobile.databinding.ActivityAddInstanceBinding
import com.nextgis.nextgismobile.util.NonNullObservableField
import com.nextgis.nextgismobile.util.statusBarHeight
import com.nextgis.nextgismobile.util.tint
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.activity_new_layer.*


class AddInstanceActivity : BaseActivity() {
    private lateinit var binding: ActivityAddInstanceBinding
    val instance = NonNullObservableField(Instance("", "", "", "", ""))

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_instance)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val params = binding.root.layoutParams as FrameLayout.LayoutParams
        params.topMargin = statusBarHeight

        binding.apply {
            activity = this@AddInstanceActivity
            fab.tint(R.color.white)
        }

        binding.executePendingBindings()
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

    fun save() {
        API.getCatalog()?.let {
            val connection = NGWConnection(instance.get().url, instance.get().login, instance.get().password)
            val status = connection.check()
            if (status) {
                it.createConnection(instance.get().url, connection)
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                toast(R.string.connection_error)
            }
        }
    }

    companion object {
        const val ADD_INSTANCE_REQUEST = 145
    }
}