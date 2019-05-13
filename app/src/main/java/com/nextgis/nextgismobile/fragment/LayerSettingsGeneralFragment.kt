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
import com.appyvet.materialrangebar.RangeBar
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.data.Layer
import com.nextgis.nextgismobile.databinding.FragmentLayerSettingsGeneralBinding


class LayerSettingsGeneralFragment(layer: Layer) : LayerSettingsBaseFragment(layer) {
    private lateinit var binding: FragmentLayerSettingsGeneralBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_layer_settings_general, container, false)
        binding.layer = layer

        updateHint(layer.minZoom, 18)
        binding.zoomRange.setRangePinsByIndices(layer.minZoom, 18)
        binding.zoomRange.setOnRangeBarChangeListener(object : RangeBar.OnRangeBarChangeListener {
            override fun onTouchEnded(rangeBar: RangeBar?) {

            }

            override fun onRangeChangeListener(rangeBar: RangeBar?, leftPinIndex: Int, rightPinIndex: Int, leftPinValue: String?, rightPinValue: String?) {
                updateHint(leftPinIndex, rightPinIndex)
            }

            override fun onTouchStarted(rangeBar: RangeBar?) {

            }
        })

        binding.executePendingBindings()
        return binding.root
    }

    private fun updateHint(leftPinIndex: Int, rightPinIndex: Int) {
        // TODO units
        binding.hint.text = getString(R.string.zoom_hint, leftPinIndex, 4f, "km", rightPinIndex, 900f, "m")
    }
}