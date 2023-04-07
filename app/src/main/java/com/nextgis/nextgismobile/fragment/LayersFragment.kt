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

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemDragListener
//import com.nextgis.maplib.Instance
//import com.nextgis.maplib.adapter.OnInstanceClickListener
//import com.nextgis.maplib.fragment.SelectInstanceDialog
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.activity.AddRemoteLayerActivity
import com.nextgis.nextgismobile.activity.MainActivity
import com.nextgis.nextgismobile.activity.NewEmptyLayerActivity
import com.nextgis.nextgismobile.activity.SelectFileActivity
import com.nextgis.nextgismobile.adapter.LayerAdapter
import com.nextgis.nextgismobile.adapter.OnLayerClickListener
import com.nextgis.nextgismobile.data.Layer
import com.nextgis.nextgismobile.databinding.FragmentLayersBinding
import com.nextgis.nextgismobile.fragment.layers.LayerSettingsFragment
import com.nextgis.nextgismobile.util.tint
import com.nextgis.nextgismobile.viewmodel.MapViewModel
import com.pawegio.kandroid.IntentFor
import com.pawegio.kandroid.toast


class LayersFragment : BaseFragment(), OnLayerClickListener {

    private var _binding: FragmentLayersBinding? = null
    private val binding get() = _binding!!

    private var bottomSheet: AddLayerDialog? = null

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                toast(R.string.not_implemented)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentLayersBinding.inflate(inflater, container, false)

        binding.apply {
            fragment = this@LayersFragment

            activity?.let { activity ->
                val mapModel = ViewModelProvider(activity).get(MapViewModel::class.java)
                mapModel.layers.observe(this@LayersFragment, Observer { layers ->
                    layers?.let {
                        (list.adapter as? LayerAdapter)?.items?.clear()
                        (list.adapter as? LayerAdapter)?.items?.addAll(layers)
                        (list.adapter as? LayerAdapter)?.dataSet = layers
                    }
                })
                mapModel.getLayers()
            }

            list.adapter = LayerAdapter(arrayListOf(), this@LayersFragment)
            list.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            list.orientation = DragDropSwipeRecyclerView.ListOrientation.VERTICAL_LIST_WITH_VERTICAL_DRAGGING
            list.dragListener = object : OnItemDragListener<Layer> {
                override fun onItemDragged(previousPosition: Int, newPosition: Int, item: Layer) {

                }

                override fun onItemDropped(initialPosition: Int, finalPosition: Int, item: Layer) {
                    if (initialPosition == finalPosition)
                        return
                    activity?.let { activity ->
                        val mapModel = ViewModelProvider(activity).get(MapViewModel::class.java)
                        mapModel.load(activity)?.let { map ->
                            map.getLayer(initialPosition)?.let { layer ->
                                map.reorder(map.getLayer(finalPosition - 1), layer)
                                map.save()
                            }
                        }
                    }
                }
            }

            setupStatus(status)
            setupToolbar(toolbar, R.string.layers)
            toolbar.inflateMenu(R.menu.menu_layers)
            toolbar.setOnMenuItemClickListener { onOptionsItemSelected(it) }

            fab.tint(R.color.colorButton)
            hideBottomSheet()
        }
        binding.executePendingBindings()
        return binding.root
    }

    private fun hideBottomSheet() {
        bottomSheet?.dismiss()
    }

    fun create() {
        bottomSheet = AddLayerDialog()
        bottomSheet?.show(this)
    }

    fun createEmpty() {
        activity?.let {
            val intent = IntentFor<NewEmptyLayerActivity>(it)
            startActivityForResult(intent, NEW_EMPTY_LAYER_REQUEST)
        }
        hideBottomSheet()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            NEW_EMPTY_LAYER_REQUEST -> { checkResult(resultCode, R.string.layer_created) }
            SELECT_FILE_REQUEST -> { checkResult(resultCode, R.string.layer_loaded) }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun createFromFile() {
        activity?.let {
            val intent = IntentFor<SelectFileActivity>(it)
            startActivityForResult(intent, SELECT_FILE_REQUEST)
        }
        hideBottomSheet()
    }

    fun addFromQMS() {
        toast(R.string.not_implemented)
        hideBottomSheet()
    }

    fun addFromNGW() {
        activity?.let {
//            SelectInstanceDialog().show(it, object : OnInstanceClickListener {
//                override fun onInstanceClick(instance: Instance) {
//                    createFromInstance(instance)
//                }
//            })
        }
        hideBottomSheet()
    }

//    fun createFromInstance(instance: Instance) {
//        context?.let {
//            val intent = IntentFor<AddRemoteLayerActivity>(it)
//            intent.putExtra("instance", instance.url)
//            startActivity(intent)
//        }
//    }

    private fun checkResult(resultCode: Int, @StringRes info: Int) {
        if (resultCode == Activity.RESULT_OK) {
            (activity as? MainActivity)?.let {
                it.snackbar(info)
                val mapModel = ViewModelProvider(it).get(MapViewModel::class.java)
                mapModel.getLayers()
            }
        }
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

    companion object {
        const val NEW_EMPTY_LAYER_REQUEST = 733
        const val SELECT_FILE_REQUEST = 537
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}