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

package com.nextgis.nextgismobile.fragment

import android.annotation.SuppressLint
import android.content.Context

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.nextgis.nextgismobile.databinding.FragmentSignupBinding
import com.nextgis.nextgismobile.model.SettingsModel
import com.nextgis.nextgismobile.viewmodel.AuthViewModel


class SignupFragment : AuthFragment() {
    override val listener: DrawableClick
        get() = this

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        val authModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        binding.apply {
            auth = authModel
            fragment = this@SignupFragment
            login.setOnEditorActionListener(onEditorListener)
            password.setOnEditorActionListener(onEditorListener)
            password.setOnTouchListener(onTouchListener)
            terms.movementMethod = LinkMovementMethod.getInstance()
        }
        binding.executePendingBindings()
        return binding.root
    }

    override fun authenticate() {
        binding.auth?.let {
            if (credentialsValid(it)) {
                it.signUp() // this.requireContext().getSharedPreferences(SettingsModel.PREF, Context.MODE_MULTI_PROCESS))
                hideKB()
            }
        }
    }

    override fun onEyeClicked() {
        binding.auth?.let {
            it.passwordVisible.set(!it.passwordVisible.get())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}