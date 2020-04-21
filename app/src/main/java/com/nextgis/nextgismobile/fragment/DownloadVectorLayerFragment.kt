/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2020 NextGIS, info@nextgis.com
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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.adapter.RasterLayersAdapter
import com.nextgis.nextgismobile.data.RasterLayer
import com.nextgis.nextgismobile.data.VectorLayer
import com.nextgis.nextgismobile.databinding.FragmentLayerDownloadVectorBinding
import com.nextgis.nextgismobile.databinding.ItemVectorLayerBinding
import com.nextgis.nextgismobile.viewmodel.LayerDownloadViewModel


class DownloadVectorLayerFragment : BaseFragment() {
    private lateinit var binding: FragmentLayerDownloadVectorBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_layer_download_vector, container, false)

        binding.apply {
            activity?.let { activity ->
                val downloadModel = ViewModelProvider(activity).get(LayerDownloadViewModel::class.java)
                model = downloadModel

                // TODO real data
                val vector = VectorLayer(0, null)
                vector.title = "Vector layer form"
                val layerForms = arrayListOf(vector)
                for (form in layerForms) {
                    val view = ItemVectorLayerBinding.inflate(inflater, forms, false)
                    view.layer = form
                    forms.addView(view.root)
                }

                // TODO real data
                val raster = RasterLayer(0, null)
                raster.title = "Raster layer"
                layers.adapter = RasterLayersAdapter(arrayListOf(raster))
                layers.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                layers.isNestedScrollingEnabled = false
            }
        }
        binding.executePendingBindings()
        return binding.root
    }
}