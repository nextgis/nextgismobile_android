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

package com.nextgis.nextgismobile.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.data.VectorLayer
import com.nextgis.nextgismobile.databinding.FragmentLayerSettingsStyleVectorBinding
import com.nextgis.nextgismobile.util.setupDropdown
import com.pawegio.kandroid.toast


open class LayerSettingsStyleVectorFragment(private val vectorLayer: VectorLayer) : LayerSettingsBaseFragment(vectorLayer) {
    private lateinit var binding: FragmentLayerSettingsStyleVectorBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_layer_settings_style_vector, container, false)
        binding.apply {
            binding.layer = vectorLayer
            binding.fragment = this
            val modeCallback = { value: String -> vectorLayer.styleMode = value }
            mode.setupDropdown(R.array.style_mode, R.array.style_mode_value, vectorLayer.styleMode, modeCallback)
            val sourceCallback = { value: String -> vectorLayer.sourceField = value }
            field.setupDropdown(R.array.source_field, R.array.source_field_value, vectorLayer.sourceField, sourceCallback)
            val fontCallback = { value: String -> vectorLayer.font = value }
            font.setupDropdown(R.array.font, R.array.font_value, vectorLayer.font, fontCallback)
            val fontSizeCallback = { value: String -> vectorLayer.fontSize = value }
            fontSize.setupDropdown(R.array.font_size, R.array.font_size_value, vectorLayer.fontSize, fontSizeCallback)
            val alignmentCallback = { value: String -> vectorLayer.alignment = value }
            alignment.setupDropdown(R.array.alignment, R.array.alignment_value, vectorLayer.alignment, alignmentCallback)

        }
        binding.executePendingBindings()
        return binding.root
    }

    fun fontColor() {
        toast(R.string.not_implemented)
    }

    fun fontStrokeColor() {
        toast(R.string.not_implemented)
    }
}