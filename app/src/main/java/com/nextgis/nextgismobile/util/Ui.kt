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

@file:Suppress("NOTHING_TO_INLINE")

package com.nextgis.nextgismobile.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.text.InputType
import androidx.annotation.ColorRes
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.util.DisplayMetrics
import android.view.View
import android.widget.AutoCompleteTextView
import androidx.core.content.ContextCompat


inline fun Context.getColorCompat(color: Int) = ContextCompat.getColor(this, color)

inline fun FloatingActionButton.tint(@ColorRes resId: Int) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        this.backgroundTintList = ColorStateList.valueOf(this.context.getColorCompat(resId))
}

inline fun View.tint(@ColorRes resId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        this.backgroundTintList = ColorStateList.valueOf(this.context.getColorCompat(resId))
}

inline val Context.statusBarHeight: Int
    get() {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

inline fun Context.dpToPx(dp: Int): Int {
    val dm = resources.displayMetrics
    return Math.round(dp * (dm.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}

@SuppressLint("ClickableViewAccessibility")
inline fun AutoCompleteTextView.setup() {
    this.setOnTouchListener { spinner, _ ->
        (spinner as AutoCompleteTextView).showDropDown()
        false
    }
    this.onFocusChangeListener = View.OnFocusChangeListener { v, b ->
        val spinner = v as AutoCompleteTextView
        if (b && spinner.text.isEmpty()) {
            spinner.showDropDown()
        }
    }
    this.inputType = InputType.TYPE_NULL
}