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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.nextgis.nextgismobile.databinding.DialogAddLayerBinding


class AddLayerDialog : BottomSheetDialogFragment() {

    private var _binding: DialogAddLayerBinding? = null
    private val binding get() = _binding!!

    //private lateinit var binding: DialogAddLayerBinding
    private var fragment: LayersFragment? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        _binding = DialogAddLayerBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        binding.fragment = fragment
        return dialog
    }

    fun show(fragment: LayersFragment) {
        this.fragment = fragment
        fragment.activity?.let {
            show(it.supportFragmentManager, TAG)
        }
    }

    companion object {
        const val TAG = "AddLayerBottomSheet"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}