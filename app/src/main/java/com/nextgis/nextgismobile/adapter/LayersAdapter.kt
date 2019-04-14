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

package com.nextgis.nextgismobile.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.nextgis.nextgismobile.data.Layer
import com.nextgis.nextgismobile.databinding.ItemLayerBinding

interface OnLayerClickListener {
    fun onVisibilityClick(layer: Layer)
    fun onZoomClick(layer: Layer)
    fun onTableClick(layer: Layer)
    fun onSettingsClick(layer: Layer)
    fun onSyncClick(layer: Layer)
    fun onShareClick(layer: Layer)
    fun onDeleteClick(layer: Layer)
    fun onMoreClick(layer: Layer)
}

class LayerAdapter(val items: List<Layer>, val listener: OnLayerClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemLayerBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        (holder as ViewHolder).bind(items[position], listener)

    override fun getItemCount(): Int = items.size

    class ViewHolder(private var binding: ItemLayerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(repo: Layer, listener: OnLayerClickListener) {
            binding.layer = repo
            binding.visibility.setOnClickListener { listener.onVisibilityClick(repo) }
            binding.more.setOnClickListener { listener.onMoreClick(repo) }
            binding.executePendingBindings()
        }
    }
}