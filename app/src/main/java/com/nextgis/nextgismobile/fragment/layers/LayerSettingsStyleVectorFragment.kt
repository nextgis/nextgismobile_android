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

package com.nextgis.nextgismobile.fragment.layers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import com.nextgis.maplib.FeatureClass
import com.nextgis.maplib.Geometry
import com.nextgis.nextgismobile.BR
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.adapter.DropdownAdapter
import com.nextgis.nextgismobile.data.VectorLayer
import com.nextgis.nextgismobile.databinding.FragmentLayerSettingsStyleVectorBinding
import com.nextgis.nextgismobile.fragment.ColorPickerDialog
import com.nextgis.nextgismobile.util.setup
import com.nextgis.nextgismobile.util.setupDropdown


open class LayerSettingsStyleVectorFragment(private val vectorLayer: VectorLayer) : LayerSettingsBaseFragment(vectorLayer) {

    private var _binding: FragmentLayerSettingsStyleVectorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLayerSettingsStyleVectorBinding.inflate(inflater, container, false)
        binding.layer = vectorLayer
        binding.fragment = this

        binding.apply {
            val modeCallback = { value: String -> vectorLayer.styleMode = value }
            mode.setupDropdown(R.array.style_mode, R.array.style_mode_value, vectorLayer.styleMode, modeCallback)

            context?.let { context ->
                (vectorLayer.handle?.dataSource as? FeatureClass)?.fields?.let { fields ->
                    val entries = fields.map { it.alias }.toTypedArray()
                    val values = fields.map { it.name }.toTypedArray()
                    val adapter = DropdownAdapter(context, R.layout.item_dropdown, entries)
                    field.setAdapter(adapter)
                    val id = values.indexOfFirst { it == vectorLayer.sourceField }
                    field.setText(entries[if (id >= 0) id else 0])
                    field.setOnItemClickListener { _, _, position, _ -> vectorLayer.sourceField = values[position] }
                    field.setup()
                }
            }

            val fontCallback = { value: String -> vectorLayer.font = value }
            font.setupDropdown(R.array.font, R.array.font_value, vectorLayer.font, fontCallback)
            val fontSizeCallback = { value: String -> vectorLayer.fontSize = value }
            fontSize.setupDropdown(R.array.font_size, R.array.font_size_value, vectorLayer.fontSize, fontSizeCallback)
            val alignmentCallback = { value: String -> vectorLayer.alignment = value }
            alignment.setupDropdown(R.array.alignment, R.array.alignment_value, vectorLayer.alignment, alignmentCallback)

            textColor.addTextChangedListener(watcher)
            textColor.onFocusChangeListener = focusListener
            strokeColor.addTextChangedListener(watcher)
            strokeColor.onFocusChangeListener = focusListener
            tintBadge(textColorBadge, vectorLayer.fontColor)
            tintBadge(strokeColorBadge, vectorLayer.fontStrokeColor)
            vectorLayer.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    if (propertyId == BR.fontColor)
                        tintBadge(textColorBadge, vectorLayer.fontColor)
                    if (propertyId == BR.fontStrokeColor)
                        tintBadge(strokeColorBadge, vectorLayer.fontStrokeColor)
                }
            })
        }
        binding.executePendingBindings()
        return binding.root
    }

    fun fontColor() {
        binding.layer?.let {
            val dialog = ColorPickerDialog()
            dialog.show(activity, it.fontColor) { color -> it.fontColor = color }
        }
    }

    fun fontStrokeColor() {
        binding.layer?.let {
            val dialog = ColorPickerDialog()
            dialog.show(activity, it.fontStrokeColor) { color -> it.fontStrokeColor = color }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.layer?.style = binding.layer?.style
    }

    companion object {
        fun fragmentStyleForGeometryType(layer: VectorLayer): LayerSettingsStyleVectorFragment {
            return when (layer.geometryType) {
                Geometry.Type.POINT, Geometry.Type.MULTIPOINT -> LayerSettingsStyleVectorPointFragment(layer)
                Geometry.Type.LINESTRING, Geometry.Type.MULTILINESTRING -> LayerSettingsStyleVectorLineFragment(layer)
                Geometry.Type.POLYGON, Geometry.Type.MULTIPOLYGON -> LayerSettingsStyleVectorPolygonFragment(layer)
                else -> LayerSettingsStyleVectorFragment(layer)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}