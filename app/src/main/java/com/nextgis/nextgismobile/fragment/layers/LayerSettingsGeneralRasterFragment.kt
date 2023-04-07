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
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.data.RasterLayer
import com.nextgis.nextgismobile.databinding.FragmentLayerSettingsGeneralRasterBinding
import com.nextgis.nextgismobile.util.setupDropdown


class LayerSettingsGeneralRasterFragment(private val rasterLayer: RasterLayer) : LayerSettingsGeneralFragment(rasterLayer) {

    private var _binding: FragmentLayerSettingsGeneralRasterBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState) as? ScrollView
        _binding = FragmentLayerSettingsGeneralRasterBinding.inflate(inflater, container, false)
        binding.layer = rasterLayer

        binding.apply {
            val callback = { value: String -> rasterLayer.lifetime = value }
            lifetime.setupDropdown(R.array.lifetime, R.array.lifetime_value, rasterLayer.lifetime, callback)
        }

        binding.executePendingBindings()
        (view?.getChildAt(0) as? LinearLayout)?.addView(binding.root)
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}