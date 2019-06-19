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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.databinding.DataBindingUtil
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.data.VectorLayer
import com.nextgis.nextgismobile.databinding.FragmentLayerSettingsGeneralVectorBinding
import com.nextgis.nextgismobile.util.setupDropdown
import com.pawegio.kandroid.toast


class LayerSettingsGeneralVectorFragment(private val vectorLayer: VectorLayer) : LayerSettingsGeneralFragment(vectorLayer) {
    private lateinit var binding: FragmentLayerSettingsGeneralVectorBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState) as? ScrollView
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_layer_settings_general_vector, container, false)
        binding.layer = vectorLayer
        binding.fragment = this

        binding.apply {
            val modeCallback = { value: String -> vectorLayer.stickingMode = value }
            mode.setupDropdown(R.array.sticking_mode, R.array.sticking_mode_value, vectorLayer.stickingMode, modeCallback)
            val thresholdCallback = { value: String -> vectorLayer.stickingThreshold = value }
            threshold.setupDropdown(R.array.sticking_threshold, R.array.sticking_threshold_value, vectorLayer.stickingThreshold, thresholdCallback)
            val unitsCallback = { value: String -> vectorLayer.stickingUnits = value }
            units.setupDropdown(R.array.sticking_units, R.array.sticking_units_value, vectorLayer.stickingUnits, unitsCallback)
        }

        binding.executePendingBindings()
        (view?.getChildAt(0) as? LinearLayout)?.addView(binding.root)
        return view
    }

    fun rebuildCache() {
        toast(R.string.not_implemented)
    }

    fun clearFeatures() {
        toast(R.string.not_implemented)
    }

    fun stickLayers() {
        toast(R.string.not_implemented)
    }

    fun editing() {
        binding.editing.performClick()
    }

    fun sticking() {
        binding.sticking.performClick()
    }

}