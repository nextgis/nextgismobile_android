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

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.activity.MainActivity
import com.nextgis.nextgismobile.adapter.LayerAdapter
import com.nextgis.nextgismobile.adapter.OnLayerClickListener
import com.nextgis.nextgismobile.data.Layer
import com.nextgis.nextgismobile.databinding.FragmentLayersBinding
import com.nextgis.nextgismobile.util.tint
import com.pawegio.kandroid.toast


class LayersFragment : Fragment(), OnLayerClickListener {
    private lateinit var binding: FragmentLayersBinding

    private val statusBarHeight: Int
        get() {
            var result = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_search -> {
                toast(R.string.not_implemented)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_layers, container, false)
        binding.apply {
            fragment = this@LayersFragment

            val def = arrayListOf<Layer>()
            activity?.let { activity ->
                val parent = activity as? MainActivity
                val map = parent?.map
                map?.let {
                    for (i in 0 until it.layerCount) {
                        it.getLayer(i)?.let { layer ->
                            def.add(Layer(i, layer.name, layer.dataSource.type, layer.visible, layer))
                        }
                    }
                }
            }

            list.adapter = LayerAdapter(def, this@LayersFragment)
            list.layoutManager = LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)

            val params = status.layoutParams
            params.height = statusBarHeight
            status.layoutParams = params

            toolbar.setTitle(R.string.layers)
            toolbar.setNavigationIcon(R.drawable.ic_arrow_left)
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
            toolbar.inflateMenu(R.menu.menu_layers)
            toolbar.setOnMenuItemClickListener { onOptionsItemSelected(it) }

            fab.tint(R.color.colorButton)
        }
        binding.executePendingBindings()
        return binding.root
    }

    fun create() {
        toast(R.string.not_implemented)
    }

    private fun refresh() {
        val parent = activity as? MainActivity
        parent?.refresh()
    }

    override fun onVisibilityClick(layer: Layer) {
        layer.visible = !layer.visible
        refresh()
    }

    override fun onZoomClick(layer: Layer) {
        toast(R.string.not_implemented)
    }

    override fun onTableClick(layer: Layer) {
        toast(R.string.not_implemented)
    }

    override fun onSettingsClick(layer: Layer) {
        toast(R.string.not_implemented)
    }

    override fun onSyncClick(layer: Layer) {
        toast(R.string.not_implemented)
    }

    override fun onShareClick(layer: Layer) {
        toast(R.string.not_implemented)
    }

    override fun onDeleteClick(layer: Layer) {
        toast(R.string.not_implemented)
    }

    override fun onMoreClick(layer: Layer) {
        toast(R.string.not_implemented)
    }

}