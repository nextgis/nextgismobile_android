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

package com.nextgis.nextgismobile.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import com.github.paolorotolo.appintro.AppIntroFragment
import com.github.paolorotolo.appintro.model.SliderPage
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.util.getColorCompat
import com.nextgis.nextgismobile.util.tint
import com.pawegio.kandroid.startActivity


class IntroActivity : AppIntro() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val page1 = SliderPage()
        page1.title = getString(R.string.intro_title_1)
        page1.titleColor = Color.BLACK
        page1.description = getString(R.string.intro_description_1)
        page1.descColor = Color.BLACK
        page1.imageDrawable = R.drawable.logo_color
        page1.bgColor = Color.WHITE
        addSlide(AppIntroFragment.newInstance(page1))

        val page2 = SliderPage()
        page2.title = getString(R.string.intro_title_2)
        page2.titleColor = Color.BLACK
        page2.description = getString(R.string.intro_description_2)
        page2.descColor = Color.BLACK
        page2.imageDrawable = R.drawable.under_construction
        page2.bgColor = Color.WHITE
        addSlide(AppIntroFragment.newInstance(page2))

        val primary = getColorCompat(R.color.colorPrimary)
        setNextArrowColor(primary)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            skipButton.tint(R.color.white)
            doneButton.tint(R.color.white)
            setColorSkipButton(primary)
            setColorDoneText(primary)
        }

        val active = getColorCompat(R.color.indicatorActive)
        val inactive = getColorCompat(R.color.indicatorInactive)
        setIndicatorColor(active, inactive)

        BaseActivity.hideStatusBar(window)
    }

    override fun onSkipPressed(currentFragment: Fragment) {
        super.onSkipPressed(currentFragment)
        openSignin()
    }

    override fun onDonePressed(currentFragment: Fragment) {
        super.onDonePressed(currentFragment)
        openSignin()
    }

    private fun openSignin() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        preferences.edit().putBoolean("intro_shown", true).apply()
        startActivity<NGIDSigninActivity>()
    }
}
