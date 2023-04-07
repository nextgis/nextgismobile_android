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
import androidx.fragment.app.DialogFragment
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.activity.NewEmptyLayerActivity
import com.nextgis.nextgismobile.data.Field
import com.nextgis.nextgismobile.databinding.DialogAddFieldBinding
import com.nextgis.nextgismobile.util.setupDropdown

class AddFieldDialog : DialogFragment() {

    private var _binding: DialogAddFieldBinding? = null
    private val binding get() = _binding!!

    private var newField = Field("", "", com.nextgis.maplib.Field.Type.INTEGER, "")
    private var activity: NewEmptyLayerActivity? = null
    var change = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert)
        _binding = DialogAddFieldBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        binding.apply {
            binding.fragment = this@AddFieldDialog
            binding.field = newField

            val callback = { value: String -> newField.type = com.nextgis.maplib.Field.Type.from(value.toInt()) }
            binding.type.setupDropdown(R.array.field_type, R.array.field_type_value, newField.type.code.toString(), callback)
        }

        return dialog
    }

    fun show(activity: NewEmptyLayerActivity, field: Field? = null) {
        this.activity = activity
        field?.let {
            newField = it
            change = true
        }
        show(activity.supportFragmentManager, TAG)
    }

    fun addField() {
        activity?.addField(newField, change)
        dismiss()
    }

    fun cancel() {
        dismiss()
    }

    companion object {
        const val TAG = "AddNewFieldDialog"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}