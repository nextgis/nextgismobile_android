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

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.v4.graphics.drawable.DrawableCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.FragmentSettingsStyleBinding
import com.nextgis.nextgismobile.viewmodel.SettingsViewModel
import com.pawegio.kandroid.toast

class SettingsStyleFragment : SettingsFragment() {
    private lateinit var binding: FragmentSettingsStyleBinding

    private val watcher = object : TextWatcher {
        var isFormatting = false

        private fun set(editable: Editable, value: String) {
            editable.clear()
            editable.append("#")
            editable.append(value)
        }

        override fun afterTextChanged(s: Editable?) {
            s?.let {
                if (!isFormatting && it.isNotEmpty()) {
                    isFormatting = true
                    if (!it.startsWith("#")) {
                        val previous = it.toString()
                        set(it, previous)
                    }
                    if (it.length > 1 && it.indexOf("#", 1, true) > 0) {
                        var previous = it.toString().substring(1)
                        previous = previous.replace("#", "")
                        set(it, previous)
                    }
                    isFormatting = false
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    private val focusListener = View.OnFocusChangeListener { view, _ ->
        while ((view as TextInputEditText).length() < 7)
            view.append("0")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_style, container, false)
        val settingsModel = ViewModelProviders.of(requireActivity()).get(SettingsViewModel::class.java)
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

    private fun tintBadge(badge: ImageButton, color: String) {
        if (color.length > 1) {
            try {
                val value = Color.parseColor(color)
                var drawable = badge.drawable
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable, value)
                badge.setImageDrawable(drawable)
            } catch (e: IllegalArgumentException) {
            }
        }
    }

    fun fillColor() {
        toast(R.string.not_implemented)
    }

    fun strokeColor() {
        toast(R.string.not_implemented)
    }
}