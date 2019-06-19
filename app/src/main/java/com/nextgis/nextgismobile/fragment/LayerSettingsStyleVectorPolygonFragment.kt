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
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.databinding.DataBindingUtil
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.data.VectorLayer
import com.nextgis.nextgismobile.databinding.FragmentLayerSettingsStyleVectorPointBinding
import com.nextgis.nextgismobile.databinding.FragmentLayerSettingsStyleVectorPolygonBinding
import com.nextgis.nextgismobile.util.setupDropdown
import com.pawegio.kandroid.toast


class LayerSettingsStyleVectorPolygonFragment(private val vectorLayer: VectorLayer) : LayerSettingsStyleVectorFragment(vectorLayer) {
    private lateinit var binding: FragmentLayerSettingsStyleVectorPolygonBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState) as? ScrollView
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_layer_settings_style_vector_polygon, container, false)
        binding.layer = vectorLayer
        binding.fragment = this
        binding.executePendingBindings()
        view?.findViewById<FrameLayout>(R.id.style)?.addView(binding.root)
        return view
    }

    fun color() {
        toast(R.string.not_implemented)
    }

    fun strokeColor() {
        toast(R.string.not_implemented)
    }
}