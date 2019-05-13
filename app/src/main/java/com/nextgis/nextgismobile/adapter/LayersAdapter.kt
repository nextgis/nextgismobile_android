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

import android.os.Build
import android.view.*
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.data.Layer
import com.nextgis.nextgismobile.databinding.ItemLayerBinding

interface OnLayerClickListener {
    fun onVisibilityClick(layer: Layer)
    fun onZoomClick(layer: Layer)
    fun onTableClick(layer: Layer)
    fun onSettingsClick(layer: Layer)
    fun onCloudClick(layer: Layer)
    fun onShareClick(layer: Layer)
    fun onDeleteClick(layer: Layer)
}

class LayerAdapter(private val items: List<Layer>, val listener: OnLayerClickListener) :
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
            binding.more.setOnClickListener {
                val context = binding.root.context
                val wrapper = ContextThemeWrapper(context, R.style.DarkPopup)
                val menu = PopupMenu(wrapper, binding.visibility)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    menu.gravity = Gravity.END

                menu.inflate(R.menu.menu_layer)
                menu.menu.findItem(R.id.action_zoom)?.isVisible = !repo.isRaster
                menu.menu.findItem(R.id.action_table)?.isVisible = !repo.isRaster
                menu.menu.findItem(R.id.action_cloud)?.isVisible = !repo.isRaster
                menu.menu.findItem(R.id.action_share)?.isVisible = !repo.isRaster

                menu.setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.action_zoom -> listener.onZoomClick(repo)
                        R.id.action_table -> listener.onTableClick(repo)
                        R.id.action_settings -> listener.onSettingsClick(repo)
                        R.id.action_cloud -> listener.onCloudClick(repo)
                        R.id.action_share -> listener.onShareClick(repo)
                        R.id.action_delete -> listener.onDeleteClick(repo)
                    }
                    true
                }
                menu.show()
            }
            binding.executePendingBindings()
        }
    }
}