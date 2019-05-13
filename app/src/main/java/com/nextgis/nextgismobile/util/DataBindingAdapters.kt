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

package com.nextgis.nextgismobile.util

import android.content.res.ColorStateList
import androidx.databinding.BindingAdapter
import android.os.Build
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat
import android.widget.ImageView
import com.pawegio.kandroid.getColorCompat
import androidx.databinding.BindingConversion



// https://stackoverflow.com/a/35809319
@BindingAdapter("android:src")
fun setImageResource(imageView: ImageView, resource: Int) {
    imageView.setImageResource(resource)
}

@BindingAdapter("android:tint")
fun setImageTintList(imageView: ImageView, resource: Int) {
    val value = imageView.context.getColorCompat(resource)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        imageView.imageTintList = ColorStateList.valueOf(value)
    } else {
        var drawable = imageView.drawable
        drawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(drawable, value)
        imageView.setImageDrawable(drawable)
    }
}

@BindingAdapter("android:visible")
fun setViewVisibility(view: View, visible: Boolean) {
    view.visibility = if (visible) View.VISIBLE else View.GONE
}

//@BindingConversion
//fun convertBooleanToVisibility(visible: Boolean): Int {
//    return if (visible) View.VISIBLE else View.GONE
//}