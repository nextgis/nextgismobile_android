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
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.adapter.LayerAdapter
import com.nextgis.nextgismobile.data.Instance
import com.nextgis.nextgismobile.data.Layer
import com.nextgis.nextgismobile.databinding.FragmentSettingsWebInstanceBinding
import com.pawegio.kandroid.toast

class SettingsWebInstanceFragment(val instance: Instance) : SettingsFragment() {
    private lateinit var binding: FragmentSettingsWebInstanceBinding
    private lateinit var adapter: LayerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings_web_instance, container, false)
        binding.instance = instance
        binding.fragment = this
        adapter = LayerAdapter(arrayListOf(Layer(1, null)), null)
        binding.layers.adapter = adapter
        binding.layers.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        binding.executePendingBindings()
        setTitle(instance.title)
        return binding.root
    }

    fun checkConnection() {
        toast(R.string.not_implemented)
    }

    override fun onDestroy() {
        setTitle(R.string.webgis)
        super.onDestroy()
    }
}