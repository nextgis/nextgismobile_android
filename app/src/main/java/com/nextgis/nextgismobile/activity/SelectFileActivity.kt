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

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.nextgis.maplib.API
import com.nextgis.maplib.Object
import com.nextgis.maplib.StatusCode
import com.nextgis.maplib.activity.PickerActivity
import com.nextgis.maplib.fragment.FilePickerFragment
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.ActivitySelectFileBinding
import com.nextgis.nextgismobile.fragment.AddFromFileDialog
import com.nextgis.nextgismobile.fragment.FileLoadingDialog
import com.nextgis.nextgismobile.fragment.ProFeatureDialog
import com.nextgis.nextgismobile.util.statusBarHeight
import com.pawegio.kandroid.runAsync
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.activity_select_file.*
import kotlin.math.roundToInt


class SelectFileActivity : BaseActivity(), PickerActivity {
    private lateinit var binding: ActivitySelectFileBinding
    private var file: Object? = null
    private var layerTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_file)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        val params = binding.root.layoutParams as FrameLayout.LayoutParams
        params.topMargin = statusBarHeight
        binding.activity = this
        addItems()

        binding.executePendingBindings()
    }

    private fun addItems() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_READ_PERMISSIONS)
        } else {
            val picker = FilePickerFragment()
            supportFragmentManager.beginTransaction().replace(R.id.container, picker).commit()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_READ_PERMISSIONS -> {
                if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    addItems()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun root(): List<Object> {
        val children = listOf<Object>()
        API.getCatalog()?.children()?.let {
            for (child in it)
                if (child.type == 52) {
                    return child.children().toList()
                }
        }
        return children
    }

    override fun onLayerSelected(file: Object?) {
        file?.let {
            if (layerTitle == null && (file.type == 507 || file.type == 501)) { // geojson or shape
                val dialog = AddFromFileDialog(file.name.substringBeforeLast("."))
                dialog.show(this, DialogInterface.OnClickListener { _, _ ->
                    this.file = file
                    this.layerTitle = dialog.layerTitle
                    onLayerSelected(file)
                })
            }
        }

        this.file = file
        API.getStore()?.let { store ->
            layerTitle?.let { title ->
                val copyOptions = mapOf(
                    "CREATE_OVERVIEWS" to "ON",
                    "NEW_NAME" to title
                )

                val dialog = FileLoadingDialog()
                dialog.show(this, DialogInterface.OnCancelListener { toast(R.string.not_implemented) })
                runAsync {
                    val result = file?.copy(Object.Type.FC_GEOJSON, store, false, copyOptions) { status, complete, _ ->
                        runOnUiThread {
                            dialog.progress.set((complete * 100).roundToInt())
                            if (status == StatusCode.FINISHED) { // TODO statuses
                                dialog.dismiss()
                                val layer = store.child(title)!!
                                addLayer(title, layer)
                            }
                        }
                        true
                    }

                    runOnUiThread {
                        if (result == null || !result) {
                            val error = API.lastError()
                            if (error.contains("Cannot copy") && error.contains("on your plan"))
                                ProFeatureDialog().show(this, getString(R.string.add_layer), getString(R.string.feature_more_1000))
                            else
                                toast(R.string.cannot_create_layer)
                            dialog.dismiss()
                        }
                    }
                }

                this.layerTitle = null
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PRO_FEATURE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK)
                    onLayerSelected(file)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        const val REQUEST_READ_PERMISSIONS = 8346
        const val PRO_FEATURE_REQUEST_CODE = 940
    }
}