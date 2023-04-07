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
import android.widget.RadioButton
import com.nextgis.maplib.FeatureClass
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.data.Field
import com.nextgis.nextgismobile.data.VectorLayer
import com.nextgis.nextgismobile.databinding.FragmentLayerSettingsFieldsVectorBinding
import com.nextgis.nextgismobile.databinding.FragmentSettingsLocationBinding
import com.nextgis.nextgismobile.databinding.ItemFieldBinding


open class LayerSettingsFieldsVectorFragment(private val vectorLayer: VectorLayer) : LayerSettingsBaseFragment(vectorLayer) {

    private var _binding: FragmentLayerSettingsFieldsVectorBinding? = null
    private val binding get() = _binding!!

    private val layerFields = (vectorLayer.handle?.dataSource as? FeatureClass)?.fields

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLayerSettingsFieldsVectorBinding.inflate(inflater, container, false)

        binding.apply {
            layerFields?.let { items ->
                for (item in items) {
                    val layoutInflater = LayoutInflater.from(context)
                    val binding = ItemFieldBinding.inflate(layoutInflater, fields, false)
                    val field = Field(item.name, item.alias, item.type, item.defaultValue)
                    binding.field = field
                    fields.addView(binding.root)
                }

                val previous = items.indexOfFirst { it.name == vectorLayer.alias }
                (fields.getChildAt(if (previous >= 0) previous else 0) as? RadioButton)?.isChecked = true
            }
        }
        binding.executePendingBindings()
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        layerFields?.getOrNull(binding.fields.checkedRadioButtonId)?.name?.let {
            vectorLayer.alias = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}