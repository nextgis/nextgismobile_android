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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.data.Layer
import com.nextgis.nextgismobile.data.RasterLayer
import com.nextgis.nextgismobile.data.VectorLayer
import com.nextgis.nextgismobile.databinding.FragmentLayerSettingsBinding


class LayerSettingsFragment() : BaseFragment() {
    private lateinit var binding: FragmentLayerSettingsBinding
    private lateinit var adapter: SettingsPagerAdapter
    private lateinit var viewPager: ViewPager
    private lateinit var layer: Layer

    constructor(layer: Layer) : this() {
        this.layer = layer
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_layer_settings, container, false)
        binding.apply {
            setupStatus(status)
            setupToolbar(toolbar, R.string.layer_settings)
        }
        binding.executePendingBindings()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        context?.let {
            adapter = SettingsPagerAdapter(childFragmentManager, it, layer)
            viewPager = view.findViewById(R.id.pager)
            viewPager.adapter = adapter
            val tabLayout = binding.tabLayout
            tabLayout.setupWithViewPager(viewPager)
        }
    }

    class SettingsPagerAdapter(fm: FragmentManager, val context: Context, private val layer: Layer) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int = if (layer.isRaster) 2 else 3

        override fun getItem(position: Int): Fragment {
            return if (layer.isRaster) {
                when (position) {
                    0 -> LayerSettingsGeneralRasterFragment(layer as RasterLayer)
                    1 -> LayerSettingsStyleRasterFragment(layer as RasterLayer)
                    else -> LayerSettingsGeneralFragment(layer)
                }
            } else {
                when (position) {
                    0 -> LayerSettingsGeneralVectorFragment(layer as VectorLayer)
                    1 -> LayerSettingsStyleVectorFragment(layer as VectorLayer)
                    else -> LayerSettingsGeneralFragment(layer)
                }
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> context.getString(R.string.general)
                1 -> context.getString(R.string.style)
                else -> context.getString(R.string.fields)
            }
        }
    }

}