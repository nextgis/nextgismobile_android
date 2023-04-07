/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2018 NextGIS, info@nextgis.com
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
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.adapter.BackupAdapter
import com.nextgis.nextgismobile.adapter.OnBackupClickListener
import com.nextgis.nextgismobile.data.Backup
import com.nextgis.nextgismobile.databinding.FragmentSettingsBackupBinding
import com.nextgis.nextgismobile.viewmodel.SettingsViewModel
import com.pawegio.kandroid.toast

class SettingsBackupFragment : SettingsFragment(), OnBackupClickListener {

    private var _binding: FragmentSettingsBackupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBackupBinding.inflate(inflater,  container, false)
        val settingsModel = ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java)
        binding.apply {
            settings = settingsModel
            fragment = this@SettingsBackupFragment
            val def = arrayListOf<Backup>()
            def.add(Backup("0", "12.02.2018", "12:00"))
            def.add(Backup("1", "05.12.2018", "17:00"))
            list.adapter = BackupAdapter(def, this@SettingsBackupFragment)
            list.layoutManager =
                LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        }
        binding.executePendingBindings()
        setTitle(R.string.backup)
        return binding.root
    }

    override fun onSyncClick(key: String) {
        toast(R.string.not_implemented)
    }

    override fun onDeleteClick(key: String) {
        toast(R.string.not_implemented)
    }

    fun sync() {
        toast(R.string.not_implemented)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}