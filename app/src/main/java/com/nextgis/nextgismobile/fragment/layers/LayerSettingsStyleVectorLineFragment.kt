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
import android.widget.FrameLayout
import android.widget.ScrollView
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import com.nextgis.nextgismobile.BR
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.data.VectorLayer
import com.nextgis.nextgismobile.databinding.FragmentLayerSettingsStyleVectorLineBinding
import com.nextgis.nextgismobile.fragment.ColorPickerDialog
import com.pawegio.kandroid.toast


class LayerSettingsStyleVectorLineFragment(private val vectorLayer: VectorLayer) : LayerSettingsStyleVectorFragment(vectorLayer) {
    private lateinit var binding: FragmentLayerSettingsStyleVectorLineBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState) as? ScrollView
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_layer_settings_style_vector_line, container, false)
        binding.apply {
            layer = vectorLayer
            fragment = this@LayerSettingsStyleVectorLineFragment

            color.addTextChangedListener(watcher)
            color.onFocusChangeListener = focusListener
            strokeColor.addTextChangedListener(watcher)
            strokeColor.onFocusChangeListener = focusListener
            tintBadge(colorBadge, vectorLayer.fillColor)
            tintBadge(strokeColorBadge, vectorLayer.strokeColor)
            vectorLayer.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    if (propertyId == BR.fillColor)
                        tintBadge(colorBadge, vectorLayer.fillColor)
                    if (propertyId == BR.strokeColor)
                        tintBadge(strokeColorBadge, vectorLayer.strokeColor)
                }
            })
        }

        binding.executePendingBindings()
        view?.findViewById<FrameLayout>(R.id.style)?.addView(binding.root)
        return view
    }

    fun color() {
        binding.layer?.let {
            val dialog = ColorPickerDialog()
            dialog.show(activity, it.fillColor) { color -> it.fillColor = color }
        }
    }

    fun strokeColor() {
        binding.layer?.let {
            val dialog = ColorPickerDialog()
            dialog.show(activity, it.strokeColor) { color -> it.strokeColor = color }
        }
    }
}