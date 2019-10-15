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

import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageButton
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.util.statusBarHeight


open class BaseFragment : Fragment() {
    protected val watcher = object : TextWatcher {
        var isFormatting = false

        private fun set(editable: Editable, value: String) {
            editable.clear()
            editable.append("#")
            editable.append(value)
        }

        override fun afterTextChanged(s: Editable?) {
            s?.let {
                if (!isFormatting && it.isNotEmpty()) {
                    isFormatting = true
                    if (!it.startsWith("#")) {
                        val previous = it.toString()
                        set(it, previous)
                    }
                    if (it.length > 1 && it.indexOf("#", 1, true) > 0) {
                        var previous = it.toString().substring(1)
                        previous = previous.replace("#", "")
                        set(it, previous)
                    }
                    isFormatting = false
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    protected val focusListener = View.OnFocusChangeListener { view, _ ->
        while ((view as TextInputEditText).length() < 9)
            view.append("F")
    }

    protected fun tintBadge(badge: ImageButton, color: String) {
        if (color.length > 1) {
            try {
                val value = Color.parseColor(color)
                var drawable = badge.drawable
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable, value)
                badge.setImageDrawable(drawable)
            } catch (e: IllegalArgumentException) {
            }
        }
    }

    protected fun setupStatus(status: View) {
        val params = status.layoutParams
        params.height = activity?.statusBarHeight ?: 0
        status.layoutParams = params
    }

    protected fun setupToolbar(toolbar: Toolbar, @StringRes title: Int) {
        toolbar.setTitle(title)
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left)
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }

}
