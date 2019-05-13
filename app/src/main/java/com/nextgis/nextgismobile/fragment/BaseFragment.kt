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

import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.util.statusBarHeight

open class BaseFragment : Fragment() {
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