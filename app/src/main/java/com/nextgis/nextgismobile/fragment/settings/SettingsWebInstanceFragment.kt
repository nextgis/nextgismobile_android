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

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextgis.maplib.API
import com.nextgis.maplib.Instance
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.adapter.LayerAdapter
import com.nextgis.nextgismobile.data.Layer
import com.nextgis.nextgismobile.databinding.FragmentSettingsWebInstanceBinding
import com.nextgis.nextgismobile.util.Utils
import com.pawegio.kandroid.toast


class SettingsWebInstanceFragment(val instance: Instance, val parent : SettingsWebListFragment) : SettingsFragment() {

    private var _binding: FragmentSettingsWebInstanceBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: LayerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsWebInstanceBinding.inflate(inflater, container, false)
        binding.instance = instance
        binding.fragment = this
        adapter = LayerAdapter(arrayListOf(Layer(1, null)), null)
        binding.layers.adapter = adapter
        binding.layers.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        binding.executePendingBindings()
        setTitle(instance.url)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_instance, menu)
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete -> {

                Utils.alertOkCancel(context, "Delete NGW " + instance.url +" ?", object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        var result = false
                        // delete NGW instance by path (it should be unique )
                        API.getCatalog()?.let {
                            for (child in it.children()) {
                                if (child.type == 72) {
                                    val items = child.children()
                                    for (item in items){
                                        if (item.path.equals(instance.title)) {
                                            item.delete() // del NGW instance
                                            result = true
                                        }
                                        Log.e("dd", "ff")
                                    }
                                    break
                                }
                            }
                        }
                        if (result) {
                            toast("NGW was deleted")

//                            val transaction = activity?.supportFragmentManager?.beginTransaction()
//                            transaction?.remove(binding.fragment!!)?.commit()

                            activity?.onBackPressed()
                            parent.refreshInstances()
                        }
                    }
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun checkConnection() {
        toast(R.string.not_implemented)
    }

    override fun onDestroy() {
        setTitle(R.string.webgis)
        super.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}