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

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextgis.maplib.Instance
import com.nextgis.maplib.activity.AddInstanceActivity
import com.nextgis.maplib.adapter.OnInstanceClickListener
import com.nextgis.maplib.adapter.getInstances
import com.nextgis.maplib.adapter.replaceInstances
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.FragmentSettingsWebListBinding
import com.nextgis.nextgismobile.viewmodel.SettingsViewModel
import com.pawegio.kandroid.IntentFor


class SettingsWebListFragment : SettingsFragment()
    , OnInstanceClickListener
{
    private var _binding: FragmentSettingsWebListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsWebListBinding.inflate(inflater, container, false)
        val settingsModel = ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java)
        binding.settings = settingsModel
        binding.fragment = this

        binding.list.adapter = getInstances(this)
        binding.list.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)

        binding.executePendingBindings()
        return binding.root
    }



    override fun onInstanceClick(instance: Instance) {
        val settings = SettingsWebInstanceFragment(instance, this)
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.add(R.id.container, settings)?.addToBackStack("instance_settings")?.commitAllowingStateLoss()
    }

    fun add() {
        context?.let { startActivityForResult(IntentFor<AddInstanceActivity>(it), AddInstanceActivity.ADD_INSTANCE_REQUEST) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            AddInstanceActivity.ADD_INSTANCE_REQUEST -> {
                if (resultCode == Activity.RESULT_OK)
                    refreshInstances()
            }
            else ->
                super.onActivityResult(requestCode, resultCode, data)
        }
    }

    public fun refreshInstances(){
        replaceInstances(binding.list.adapter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}