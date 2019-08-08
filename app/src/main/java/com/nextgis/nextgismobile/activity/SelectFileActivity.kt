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
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextgis.maplib.API
import com.nextgis.maplib.Object
import com.nextgis.maplib.StatusCode
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.adapter.FilesAdapter
import com.nextgis.nextgismobile.adapter.OnFileClickListener
import com.nextgis.nextgismobile.databinding.ActivitySelectFileBinding
import com.nextgis.nextgismobile.fragment.AddFromFileDialog
import com.nextgis.nextgismobile.fragment.FileLoadingDialog
import com.nextgis.nextgismobile.fragment.ProFeatureDialog
import com.nextgis.nextgismobile.util.NonNullObservableField
import com.nextgis.nextgismobile.util.statusBarHeight
import com.pawegio.kandroid.runAsync
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.activity_new_layer.*
import java.util.*
import kotlin.math.roundToInt

class SelectFileActivity : BaseActivity(), OnFileClickListener {
    private lateinit var binding: ActivitySelectFileBinding
    private val stack = Stack<Object>()
    private var file: Object? = null
    private var layerTitle = ""
    val path = NonNullObservableField("/")

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_picker, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                val file = stack.pop()
                file.refresh()
                onFileClick(file)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addItems() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_READ_PERMISSIONS)
        } else {
            list.adapter = FilesAdapter(root(), this)
            list.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
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

    private fun root(): ArrayList<Object> {
        val children = arrayListOf<Object>()
        API.getCatalog()?.children()?.let { addItems(children, it.toList()) }
        return children
    }

    override fun onFileClick(file: Object) {
        (list.adapter as? FilesAdapter)?.items?.clear()
        val children = arrayListOf<Object>()
        // TODO gpkg/ngrc type
//        if (file.type == 501) { // TODO shp type
//            toast(R.string.not_implemented)
//            return
//        }
        if (file.type == 55) { // TODO zip type
            toast(R.string.not_implemented)
            return
        }
        if (file.type == 507 || file.type == 501) { // geojson
            val dialog = AddFromFileDialog(file.name.substringBeforeLast("."))
            dialog.show(this, DialogInterface.OnClickListener { _, _ ->
                this.file = file
                this.layerTitle = dialog.layerTitle
                addLayer()
            })
            return
        }
        if (file.type == 1002) { // TODO tif type
            toast(R.string.not_implemented)
            return
        }

        if (file.type == -999) {
            stack.pop()
            if (stack.size == 0) {
                addItems(children, root())
                path.set("/")
            } else {
                addBack(children)
                val parent = stack.peek()
                addItems(children, parent.children().toList())
                path.set(parent.path)
            }
        } else {
            addBack(children)
            addItems(children, file.children().toList())
            stack.add(file)
            path.set(file.path)
        }
        (list.adapter as? FilesAdapter)?.items?.addAll(children)
        list.adapter?.notifyDataSetChanged()
    }

    private fun addLayer() {
        API.getStore()?.let { store ->
            val copyOptions = mapOf(
                "CREATE_OVERVIEWS" to "ON",
                "NEW_NAME" to layerTitle
            )

            val dialog = FileLoadingDialog()
            dialog.show(this, DialogInterface.OnCancelListener { toast(R.string.not_implemented) })
            runAsync {
                val result = file?.copy(Object.Type.FC_GEOJSON, store, false, copyOptions) { status, complete, _ ->
                    runOnUiThread {
                        dialog.progress.set((complete * 100).roundToInt())
                        if (status == StatusCode.FINISHED) { // TODO statuses
                            dialog.dismiss()
                            val layer = store.child(layerTitle)!!
                            addLayer(layerTitle, layer)
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
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            PRO_FEATURE_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK)
                    addLayer()
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun addBack(children: ArrayList<Object>) {
        children.add(Object(getString(R.string.back), -999, "", -1))
    }

    private fun addItems(children: ArrayList<Object>, items: List<Object>) {
        children.addAll(items.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { it.name })).sortedBy { it.type != 53 })
    }

    companion object {
        const val REQUEST_READ_PERMISSIONS = 8346
        const val PRO_FEATURE_REQUEST_CODE = 940
    }
}