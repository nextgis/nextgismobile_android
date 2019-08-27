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
import android.os.Bundle
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextgis.maplib.API
import com.nextgis.maplib.Geometry
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.adapter.FieldsAdapter
import com.nextgis.nextgismobile.adapter.OnFieldClickListener
import com.nextgis.nextgismobile.data.Field
import com.nextgis.nextgismobile.databinding.ActivityNewLayerBinding
import com.nextgis.nextgismobile.fragment.AddFieldDialog
import com.nextgis.nextgismobile.util.setupDropdown
import com.nextgis.nextgismobile.util.statusBarHeight
import com.nextgis.nextgismobile.util.tint
import com.nextgis.nextgismobile.viewmodel.LayerViewModel
import com.pawegio.kandroid.toast
import kotlinx.android.synthetic.main.activity_new_layer.*

class NewEmptyLayerActivity : BaseActivity(), OnFieldClickListener {
    override fun onEditClick(field: Field) {
        AddFieldDialog().show(this, field)
    }

    override fun onDeleteClick(field: Field) {
        binding.model?.deleteField(field)
        binding.list.adapter?.notifyDataSetChanged()
    }

    private lateinit var binding: ActivityNewLayerBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_layer)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        val params = binding.root.layoutParams as FrameLayout.LayoutParams
        params.topMargin = statusBarHeight

        val layerModel = ViewModelProviders.of(this).get(LayerViewModel::class.java)

        binding.apply {
            model = layerModel
            activity = this@NewEmptyLayerActivity

            val callback = { value: String -> layerModel.vectorLayer.geometryType = Geometry.Type.from(value.toInt()) }
            type.setupDropdown(R.array.geometry_type, R.array.geometry_type_value, "", callback)

            layerModel.vectorLayer.geometryType = Geometry.Type.POINT
            layerModel.fields.observe(this@NewEmptyLayerActivity, Observer { fields ->
                fields?.let {
                    (list.adapter as? FieldsAdapter)?.items?.clear()
                    (list.adapter as? FieldsAdapter)?.items?.addAll(fields)
                    list.adapter?.notifyDataSetChanged()
                }
            })

            list.adapter = FieldsAdapter(arrayListOf(), this@NewEmptyLayerActivity)
            list.layoutManager = LinearLayoutManager(this@NewEmptyLayerActivity, RecyclerView.VERTICAL, false)
            layerModel.init()

            fab.tint(R.color.white)
        }

        binding.executePendingBindings()
    }

    fun save() {
        API.getStore()?.let { store ->
            val options = mapOf(
                "CREATE_OVERVIEWS" to "ON",
                "ZOOM_LEVELS" to "2,3,4,5,6,7,8,9,10,11,12,13,14"
            )

            binding.model?.let {
                val fields = arrayListOf<com.nextgis.maplib.Field>()
                for (field in it.fields.value!!) {
                    fields.add(com.nextgis.maplib.Field(field.name, field.alias, field.type, field.def))
                }

                val newLayer = store.createFeatureClass(it.vectorLayer.title, it.vectorLayer.geometryType, fields, options)
                if (newLayer != null) {
                    addLayer(it.vectorLayer.title, newLayer)
                } else {
                    toast(R.string.cannot_create_layer)
                }
            }
        }
    }

    fun addField() {
        AddFieldDialog().show(this)
    }

    fun addField(field: Field, change: Boolean = false) {
        if (!change)
            binding.model?.addField(field)
        binding.list.adapter?.notifyDataSetChanged()
    }
}