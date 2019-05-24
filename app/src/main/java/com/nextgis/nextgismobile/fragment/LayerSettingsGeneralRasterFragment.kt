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

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.FragmentLayerSettingsGeneralRasterBinding
import com.nextgis.nextgismobile.data.RasterLayer
import android.widget.*
import com.nextgis.nextgismobile.adapter.LifetimeAdapter


class LayerSettingsGeneralRasterFragment(private val rasterLayer: RasterLayer) : LayerSettingsGeneralFragment(rasterLayer) {
    private lateinit var binding: FragmentLayerSettingsGeneralRasterBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState) as? ScrollView
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_layer_settings_general_raster, container, false)
        binding.layer = rasterLayer

        binding.apply {
            val entries = resources.getStringArray(R.array.lifetime)
            val values = resources.getStringArray(R.array.lifetime_value)
            context?.let {
                val adapter = LifetimeAdapter(it, R.layout.item_dropdown, entries)
                lifetime.setAdapter(adapter)
            }

            var id = values.indexOf(rasterLayer.lifetime)
            id = if (id >= 0) id else 2
            lifetime.setText(entries[id])

            lifetime.setOnItemClickListener { _, _, position, _ -> rasterLayer.lifetime = values[position] }
            lifetime.setOnTouchListener { spinner, _ ->
                (spinner as AutoCompleteTextView).showDropDown()
                false
            }
            lifetime.onFocusChangeListener = View.OnFocusChangeListener { v, b ->
                val spinner = v as AutoCompleteTextView
                if (b && spinner.text.isEmpty()) {
                    spinner.showDropDown()
                }
            }
            lifetime.inputType = InputType.TYPE_NULL
        }

        binding.executePendingBindings()
        (view?.getChildAt(0) as? LinearLayout)?.addView(binding.root)
        return view
    }

}