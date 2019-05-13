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

import android.preference.PreferenceManager
import androidx.annotation.ArrayRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.activity.SettingsActivity

open class SettingsFragment : Fragment() {
    open fun switch(checked: Boolean, key: String) {
        PreferenceManager.getDefaultSharedPreferences(requireActivity()).edit().putBoolean(key, checked).apply()
    }

    protected fun setTitle(@StringRes res: Int) {
        (activity as? SettingsActivity)?.let { it.supportActionBar?.setTitle(res) }
    }

    protected fun showDialog(@ArrayRes entries: Int, @ArrayRes values: Int, default: String, @StringRes title: Int, listener: (value: String) -> Unit) {
        context?.let { context ->
            val array = context.resources.getStringArray(entries)
            val value = context.resources.getStringArray(values)
            val id = value.indexOf(default)
            val builder = AlertDialog.Builder(context, R.style.AlertDialog)
                .setTitle(title)
                .setSingleChoiceItems(array, id) { _, i -> listener(value[i]) }
                .setPositiveButton(android.R.string.ok, null).create()
            builder.show()
        }
    }
}