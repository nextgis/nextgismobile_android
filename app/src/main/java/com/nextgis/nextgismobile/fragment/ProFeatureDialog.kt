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

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.activity.NGIDSigninActivity
import com.nextgis.nextgismobile.activity.SelectFileActivity
import com.nextgis.nextgismobile.databinding.DialogProFeatureBinding
import com.pawegio.kandroid.IntentFor


class ProFeatureDialog : DialogFragment() {
    private lateinit var binding: DialogProFeatureBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_pro_feature, null, false)
        dialog.setContentView(binding.root)
        binding.apply {
            fragment = this@ProFeatureDialog
            title.text = arguments?.getString("title")
            info.text = arguments?.getString("info")
        }

        return dialog
    }

    fun show(activity: FragmentActivity?, title: String, info: String) {
        activity?.let {
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("info", info)
            arguments = bundle
            show(it.supportFragmentManager, TAG)
        }
    }

    fun unlock() {
        activity?.let { it.startActivityForResult(IntentFor<NGIDSigninActivity>(it), SelectFileActivity.PRO_FEATURE_REQUEST_CODE) }
        dismiss()
    }

    fun cancel() {
        dismiss()
    }

    companion object {
        const val TAG = "ProFeatureDialog"
    }
}