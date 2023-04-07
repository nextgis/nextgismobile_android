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
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.nextgis.maplib.util.NonNullObservableField
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.DialogFileLoadingBinding


class FileLoadingDialog() : DialogFragment() {

    private var _binding: DialogFileLoadingBinding? = null
    private val binding get() = _binding!!

    private var listener: DialogInterface.OnCancelListener? = null
    val progress = NonNullObservableField(0)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert)
        _binding = DialogFileLoadingBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        binding.fragment = this
        return dialog
    }

    fun show(activity: FragmentActivity?, listener: DialogInterface.OnCancelListener) {
        this.listener = listener
        activity?.let {
            show(it.supportFragmentManager, TAG)
        }
    }

    fun cancel() {
        listener?.onCancel(dialog)
        dismiss()
    }

    companion object {
        const val TAG = "FileLoadingDialog"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}