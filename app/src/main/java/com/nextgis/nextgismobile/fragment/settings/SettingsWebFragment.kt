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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProvider

import androidx.viewpager.widget.ViewPager
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.FragmentSettingsWebBinding
import com.nextgis.nextgismobile.viewmodel.SettingsViewModel

class SettingsWebFragment : SettingsFragment() {

    private var _binding: FragmentSettingsWebBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: WebGISPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsWebBinding.inflate(inflater, container, false)
        val settingsModel = ViewModelProvider(requireActivity()).get(SettingsViewModel::class.java)
        binding.settings = settingsModel
        binding.executePendingBindings()
        setTitle(R.string.webgis)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        context?.let {
            adapter = WebGISPagerAdapter(childFragmentManager, it)
            viewPager = view.findViewById(R.id.pager)
            viewPager.adapter = adapter
            val tabLayout = binding.tabLayout
            tabLayout.setupWithViewPager(viewPager)
        }
    }

    class WebGISPagerAdapter(fm: FragmentManager, val context: Context) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int = 2

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> SettingsWebListFragment()
                1 -> SettingsWebParamsFragment()
                else -> SettingsWebParamsFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> context.getString(R.string.list)
                1 -> context.getString(R.string.params)
                else -> context.getString(R.string.params)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}