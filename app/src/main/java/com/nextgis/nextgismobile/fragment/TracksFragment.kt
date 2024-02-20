/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2023 NextGIS, info@nextgis.com
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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemDragListener
import com.nextgis.maplib.API
import com.nextgis.maplib.Constants
import com.nextgis.maplib.Instance
import com.nextgis.maplib.adapter.OnInstanceClickListener
import com.nextgis.maplib.fragment.SelectInstanceDialog
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.activity.AddRemoteLayerActivity
import com.nextgis.nextgismobile.activity.BaseActivity
import com.nextgis.nextgismobile.activity.MainActivity
import com.nextgis.nextgismobile.activity.NewEmptyLayerActivity
import com.nextgis.nextgismobile.activity.SelectFileActivity
import com.nextgis.nextgismobile.adapter.LayerAdapter
import com.nextgis.nextgismobile.adapter.OnLayerClickListener
import com.nextgis.nextgismobile.adapter.TrackAdapter
import com.nextgis.nextgismobile.data.Layer
import com.nextgis.nextgismobile.databinding.FragmentLayersBinding
import com.nextgis.nextgismobile.databinding.FragmentTracksBinding
import com.nextgis.nextgismobile.fragment.layers.LayerSettingsFragment
import com.nextgis.nextgismobile.util.tint
import com.nextgis.nextgismobile.viewmodel.MapViewModel
import com.pawegio.kandroid.IntentFor
import com.pawegio.kandroid.toast

class TracksFragment : BaseFragment(), OnLayerClickListener {

    private var _binding: FragmentTracksBinding? = null
    private val binding get() = _binding!!

    private var mTracksAdapter: TrackAdapter? = null


//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.action_search -> {
//                toast(R.string.not_implemented)
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentTracksBinding.inflate(inflater, container, false)

        binding.apply {
            fragment = this@TracksFragment

            activity?.let { activity ->

            }

            setupStatus(status)
            setupToolbar(toolbar, R.string.tracks_list)
            toolbar.inflateMenu(R.menu.menu_layers)
            toolbar.setOnMenuItemClickListener { onOptionsItemSelected(it) }

            val store = API.getStore()
            val tracksTable = store?.trackTable()
            if (tracksTable != null) {
                binding.tracksList.setHasFixedSize(true)
                mTracksAdapter = TrackAdapter(container!!.context, tracksTable)
                binding.tracksList.layoutManager = LinearLayoutManager(container!!.context)
                binding.tracksList.adapter = mTracksAdapter
            }

            val sharedPref = PreferenceManager.getDefaultSharedPreferences(container!!.context)
            val sendInterval = sharedPref.getInt("sendInterval", 1200).toLong()
            val syncWithNGW = sharedPref.getBoolean(Constants.Settings.sendTracksToNGWKey, false)
            if (syncWithNGW) {
                (activity as BaseActivity).enableSync(sendInterval)
            } else {
                (activity as BaseActivity).disableSync()
            }
        }
        binding.executePendingBindings()
        return binding.root
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
        val settings = LayerSettingsFragment(layer)
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.add(R.id.container, settings)?.addToBackStack("layer_settings")?.commitAllowingStateLoss()
    }

    override fun onCloudClick(layer: Layer) {
        toast(R.string.not_implemented)
    }

    override fun onShareClick(layer: Layer) {
        toast(R.string.not_implemented)
    }

    override fun onDeleteClick(layer: Layer) {
        activity?.let { activity ->
            val mapModel = ViewModelProvider(activity).get(MapViewModel::class.java)
            val map = mapModel.load(activity)
            map?.deleteLayer(layer.id)
            map?.save()
            mapModel.getLayers()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}