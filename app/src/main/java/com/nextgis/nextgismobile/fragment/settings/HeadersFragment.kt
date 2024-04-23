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
import android.app.Activity
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.Observable
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.request.RequestOptions
import com.nextgis.maplib.Account
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.activity.NGIDSigninActivity
import com.nextgis.nextgismobile.adapter.OnItemClickListener
import com.nextgis.nextgismobile.adapter.SettingAdapter
import com.nextgis.nextgismobile.data.Setting
import com.nextgis.nextgismobile.databinding.FragmentHeadersBinding
import com.nextgis.nextgismobile.util.tint
import com.nextgis.nextgismobile.viewmodel.AuthViewModel
import com.pawegio.kandroid.toast


class HeadersFragment : Fragment(), OnItemClickListener {

    private var _binding: FragmentHeadersBinding? = null
    private val binding get() = _binding!!
//    private lateinit var binding: FragmentHeadersBinding

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHeadersBinding.inflate(inflater, container, false)
//        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_headers, container, false)
        val authModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
//        val userModel = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.apply {
            auth = authModel
//            user = userModel
            fragment = this@HeadersFragment

            list.adapter = SettingAdapter(settings, this@HeadersFragment)
            list.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
            list.isNestedScrollingEnabled = false

            authenticate.tint(R.color.white)
        }

//        authModel.token.observe(this, Observer { token ->
//            token?.let { userModel.profile() }
//        })

        authModel.account.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                val placeholder = R.drawable.ic_account_outline_padded
                val requestOptions = RequestOptions().circleCrop().placeholder(placeholder)
//                val avatar = authModel.account.get()?.avatar
//                avatar?.apply {
//                    Glide.with(this@HeadersFragment).load(authModel.account.get()?.avatar).apply(requestOptions).into(binding.avatar)
//                }
            }
        })

        binding.executePendingBindings()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_settings, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        val topMost = requireActivity().supportFragmentManager.backStackEntryCount < 2
        binding.auth?.account?.get()?.authorized?.let { menu.findItem(R.id.action_signing).setVisible(it && topMost) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_signing -> {
                activity?.let { activity ->
                    binding.auth?.let {
                        if (it.account.get() != null &&
                            // it.account.get()!!.authorized
                            !(it.account.get() as Account ).auth.accessToken.equals(""))
                            showConfirmation(it, activity)
                        else
                            activity?.startActivityForResult( NGIDSigninActivity.getSignInIntent(context), NGIDSigninActivity.CODE_SIGN_SUCC)
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showConfirmation(auth: AuthViewModel, activity: Activity) {
        val builder = AlertDialog.Builder(activity)
            .setTitle(R.string.confirmation)
            .setMessage(R.string.ngid_disconnect)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                auth.deleteAccount()
                activity.invalidateOptionsMenu()
                // auth.
            }
            .setNegativeButton(android.R.string.cancel, null).create()
        builder.show()
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
            if (it.account.get() != null && it.account.get()!!.authorized)
                toast(R.string.not_implemented)
            else {
                activity?.startActivityForResult( NGIDSigninActivity.getSignInIntent(context), NGIDSigninActivity.CODE_SIGN_SUCC)
                //activity?.startActivity<NGIDSigninActivity>()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

}