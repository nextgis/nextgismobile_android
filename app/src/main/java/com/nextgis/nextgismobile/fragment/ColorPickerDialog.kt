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
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.nextgis.maplib.util.NonNullObservableField
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.DialogColorPickerBinding
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.skydoves.colorpickerview.sliders.AlphaSlideBar
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar


class ColorPickerDialog : DialogFragment() {
    private lateinit var binding: DialogColorPickerBinding
    val color = NonNullObservableField("")
    private var listener: ((color: String) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_color_picker, null, false)
        dialog.setContentView(binding.root)
        binding.apply {
            fragment = this@ColorPickerDialog
            val alphaSlideBar = root.findViewById<AlphaSlideBar>(R.id.alphaSlideBar)
            val brightnessSlideBar = root.findViewById<BrightnessSlideBar>(R.id.brightnessSlide)
            picker.attachAlphaSlider(alphaSlideBar)
            picker.attachBrightnessSlider(brightnessSlideBar)
            picker.fireColorListener(Color.RED, false)
            picker.setColorListener(ColorEnvelopeListener { envelope, fromUser ->
                if (fromUser)
                    color.set("#" + envelope.hexCode)
            })
        }

        return dialog
    }

    fun show(activity: FragmentActivity?, color: String, listener: (color: String) -> Unit) {
        this.listener = listener
        this.color.set(color)
        activity?.let { show(it.supportFragmentManager, TAG) }
    }

    fun apply() {
        listener?.invoke(color.get().replace("#", ""))
        dismiss()
    }

    fun cancel() {
        dismiss()
    }

    companion object {
        const val TAG = "ColorPickerDialog"
    }
}