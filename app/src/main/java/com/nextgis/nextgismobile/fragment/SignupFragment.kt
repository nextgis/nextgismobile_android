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
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.InputType
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.FragmentSignupBinding
import com.nextgis.nextgismobile.viewmodel.AuthViewModel
import com.pawegio.kandroid.toast


class SignupFragment : AuthFragment() {
    override val listener: DrawableClick
        get() = this

    private lateinit var binding: FragmentSignupBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false)
        val authModel = ViewModelProviders.of(requireActivity()).get(AuthViewModel::class.java)
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
                toast(R.string.not_implemented)
                it.signUp()
                hideKB()
            }
        }
    }

    override fun onEyeClicked() {
        binding.auth?.let {
//            val visible = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
//            val hidden = InputType.TYPE_TEXT_VARIATION_PASSWORD
            it.passwordVisible.set(!it.passwordVisible.get())
//            binding.password.inputType = if (it.passwordVisible.get()) visible else hidden
        }
    }
}