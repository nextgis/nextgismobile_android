/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2018 NextGIS, info@nextgis.com
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
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.FragmentSettingsMapBinding
import com.nextgis.nextgismobile.viewmodel.SettingsViewModel
import com.pawegio.kandroid.toast

class SettingsMapFragment : SettingsFragment() {
    private lateinit var binding: FragmentSettingsMapBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_map, container, false)
        val settingsModel = ViewModelProviders.of(requireActivity()).get(SettingsViewModel::class.java)
        binding.apply {
            fragment = this@SettingsMapFragment
            settings = settingsModel
        }
        binding.executePendingBindings()
        setTitle(R.string.map)
        return binding.root
    }

    fun style() {
        toast(R.string.not_implemented)
    }

    fun scale() {
        toast(R.string.not_implemented)
    }

    fun coordinates() {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.container, SettingsCoordinatesFragment()).addToBackStack("subsettings").commitAllowingStateLoss()
    }

    fun map() {
        context?.let { context ->
            val array = context.resources.getStringArray(R.array.map_background)
            val value = context.resources.getStringArray(R.array.map_background_value)
            binding.settings?.mapBackground?.get()?.let {
                val id = value.indexOf(it)
                val builder = AlertDialog.Builder(context)
                    .setTitle(R.string.map_background)
                    .setSingleChoiceItems(array, id) { _, i -> binding.settings?.mapBackground?.set(value[i]) }
                    .setPositiveButton(android.R.string.ok, null).create()
                builder.show()
            }
        }
    }

}