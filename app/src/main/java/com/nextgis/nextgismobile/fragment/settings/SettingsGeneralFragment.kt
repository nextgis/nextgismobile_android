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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.FragmentSettingsGeneralBinding
import com.nextgis.nextgismobile.viewmodel.SettingsViewModel
import com.pawegio.kandroid.toast

class SettingsGeneralFragment : SettingsFragment() {
    private lateinit var binding: FragmentSettingsGeneralBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_general, container, false)
        val settingsModel = ViewModelProviders.of(requireActivity()).get(SettingsViewModel::class.java)
        binding.apply {
            fragment = this@SettingsGeneralFragment
            settings = settingsModel
        }
        binding.executePendingBindings()
        setTitle(R.string.general)
        return binding.root
    }

    fun storage() {
        toast(R.string.not_implemented)
    }

}