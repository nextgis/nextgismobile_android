/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2018 NextGIS, info@nextgis.com
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

import android.annotation.SuppressLint
import com.google.android.material.textfield.TextInputEditText
import androidx.fragment.app.Fragment
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.viewmodel.AuthViewModel
import com.pawegio.kandroid.inputMethodManager
import com.pawegio.kandroid.toast


abstract class AuthFragment : Fragment(), DrawableClick {
    protected abstract val listener: DrawableClick
    protected val onEditorListener = TextView.OnEditorActionListener { _, actionId, event -> go(actionId, event) }
    protected val onTouchListener = object : View.OnTouchListener {
        val RIGHT = 2
        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            if (event.action == MotionEvent.ACTION_UP) {
                val right = (view as TextInputEditText).compoundDrawables[RIGHT]
                var leftEdgeOfRightDrawable = view.right
                leftEdgeOfRightDrawable -= right?.bounds?.width() ?: 0
                if (event.rawX >= leftEdgeOfRightDrawable) {
                    listener.onEyeClicked()
                    return true
                }
            }
            return false
        }
    }

    private fun isKeyEnter(event: KeyEvent?): Boolean {
        return event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN
    }

    private fun isGoPressed(id: Int, event: KeyEvent?): Boolean {
        return id == EditorInfo.IME_ACTION_DONE || isKeyEnter(event)
    }

    private fun go(actionId: Int, event: KeyEvent?): Boolean {
        if (isGoPressed(actionId, event))
            authenticate()
        return true
    }

    abstract fun authenticate()

    protected fun credentialsValid(auth: AuthViewModel): Boolean {
        if (!auth.isEmailValid()) {
            toast(R.string.invalid_email)
            return false
        } else if (auth.isPasswordEmpty()) {
            toast(R.string.empty_password)
            return false
        }
        return true
    }

    protected fun hideKB() {
        activity?.let {
            it.inputMethodManager?.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }
}