/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2018-2019 NextGIS, info@nextgis.com
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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.activity.NGIDSigninActivity
import com.nextgis.nextgismobile.adapter.OnItemClickListener
import com.nextgis.nextgismobile.adapter.SettingAdapter
import com.nextgis.nextgismobile.data.Setting
import com.nextgis.nextgismobile.databinding.FragmentHeadersBinding
import com.nextgis.nextgismobile.util.tint
import com.nextgis.nextgismobile.viewmodel.AuthViewModel
import com.nextgis.nextgismobile.viewmodel.UserViewModel
import com.pawegio.kandroid.startActivity
import com.pawegio.kandroid.toast


class HeadersFragment : Fragment(), OnItemClickListener {
    private lateinit var binding: FragmentHeadersBinding
    private val settings: List<Setting>
        get() {
            val array = arrayListOf<Setting>()
            array.add(Setting(getString(R.string.general), "general"))
            array.add(Setting(getString(R.string.map), "map"))
            array.add(Setting(getString(R.string.location), "location"))
            array.add(Setting(getString(R.string.webgis), "webgis"))
            array.add(Setting(getString(R.string.tracking), "tracking"))
            array.add(Setting(getString(R.string.backup), "backup"))
            return array
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_headers, container, false)
        val authModel = ViewModelProviders.of(requireActivity()).get(AuthViewModel::class.java)
        val userModel = ViewModelProviders.of(this).get(UserViewModel::class.java)

        binding.apply {
            auth = authModel
            user = userModel
            fragment = this@HeadersFragment

            list.adapter = SettingAdapter(settings, this@HeadersFragment)
            list.layoutManager =
                LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            list.isNestedScrollingEnabled = false

            authenticate.tint(R.color.white)
        }

        authModel.token.observe(this, Observer { token ->
            token?.let { userModel.profile() }
        })

        userModel.avatar.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val placeholder = R.drawable.ic_account_outline_padded
                val requestOptions = RequestOptions().circleCrop().placeholder(placeholder)
                Glide.with(this@HeadersFragment).load(userModel.avatar.get())
                    .apply(requestOptions).into(binding.avatar)
            }
        })

        binding.executePendingBindings()
        return binding.root
    }

    override fun onItemClick(key: String) {
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.container, fragment(key)).addToBackStack("settings").commitAllowingStateLoss()
    }

    private fun fragment(key: String): Fragment {
        return when (key) {
            "general" -> SettingsGeneralFragment()
            "map" -> SettingsMapFragment()
            "location" -> SettingsLocationFragment()
            "webgis" -> SettingsWebFragment()
            "tracking" -> SettingsTracksFragment()
            "backup" -> SettingsBackupFragment()
            else -> SettingsGeneralFragment()
        }
    }

    fun action() {
        binding.auth?.let {
            if (it.isAuthorized.get())
                toast(R.string.not_implemented)
            else
                activity?.startActivity<NGIDSigninActivity>()
        }
    }

}