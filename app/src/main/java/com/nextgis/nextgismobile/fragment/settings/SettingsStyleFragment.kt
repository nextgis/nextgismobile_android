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

package com.nextgis.nextgismobile.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.lifecycle.ViewModelProvider
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.FragmentSettingsStyleBinding
import com.nextgis.nextgismobile.fragment.ColorPickerDialog
import com.nextgis.nextgismobile.viewmodel.SettingsViewModel

class SettingsStyleFragment : SettingsFragment() {
    private var _binding: FragmentSettingsStyleBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsStyleBinding.inflate(inflater, container, false)
        val settingsModel = ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java)
        binding.apply {
            settings = settingsModel
            fragment = this@SettingsStyleFragment

            settingsModel.fillColor.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    tintFillBadge()
                }
            })

            settingsModel.strokeColor.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    tintStrokeBadge()
                }
            })

            tintFillBadge()
            tintStrokeBadge()
            fillColor.addTextChangedListener(watcher)
            fillColor.onFocusChangeListener = focusListener
            strokeColor.addTextChangedListener(watcher)
            strokeColor.onFocusChangeListener = focusListener
        }
        binding.executePendingBindings()
        setTitle(R.string.selection_style)
        return binding.root
    }

    fun reset() {
        binding.settings?.let {
            it.fillColor.set(it.color(getString(R.string.fill_color_default)))
            it.strokeColor.set(it.color(getString(R.string.stroke_color_default)))
            it.strokeWidth.set(getString(R.string.stroke_width_default))
        }
    }

    private fun tintFillBadge() {
        val color = binding.fillColor.text.toString()
        tintBadge(binding.fillBadge, color)
    }

    private fun tintStrokeBadge() {
        val color = binding.strokeColor.text.toString()
        tintBadge(binding.widthBadge, color)
    }

    fun fillColor() {
        activity?.let {
            val dialog = ColorPickerDialog()
            val settingsModel = ViewModelProvider(it).get(SettingsViewModel::class.java)
            dialog.show(it, settingsModel.fillColor.get()) { color -> settingsModel.fillColor.set(color) }
        }
    }

    fun strokeColor() {
        activity?.let {
            val dialog = ColorPickerDialog()
            val settingsModel = ViewModelProvider(it).get(SettingsViewModel::class.java)
            dialog.show(it, settingsModel.strokeColor.get()) { color -> settingsModel.strokeColor.set(color) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}