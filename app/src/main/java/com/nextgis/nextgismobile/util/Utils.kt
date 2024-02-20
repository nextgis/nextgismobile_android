/*
 * Project:  NextGIS Mobile
 * Purpose:  Mobile GIS for Android
 * Author:   Stanislav Petriakov, becomeglory@gmail.com
 * ****************************************************************************
 * Copyright © 2023 NextGIS, info@nextgis.com
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

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.nextgis.maplib.service.TrackerService
import com.nextgis.maplib.startTrackerService
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.activity.MainActivity

public class Utils {
    companion object {
        fun alertOkCancel(context: Context?, text: String,
                          listenerOK : DialogInterface.OnClickListener
                          //,listenerCancel : DialogInterface.OnClickListener
        ) {
            val builder = AlertDialog.Builder(context)
            builder
                .setMessage(text)
                .setNegativeButton(R.string.cancel, null)// listenerCancel)
                .setPositiveButton(R.string.ok, listenerOK)

            val dialog = builder.show()
        }
    }
}

internal fun requestPermissionsUtil(
    title: Int,
    message: Int,
    requestCode: Int,
    permissions: Array<String>,
    activity: Activity) {
    var shouldShowDialog = false
    for (permission in permissions) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            shouldShowDialog = true
            break
        }
    }
    if (shouldShowDialog) {
        val activity: Activity = activity
        val builder = androidx.appcompat.app.AlertDialog.Builder(activity).setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null).create()

        builder.setCanceledOnTouchOutside(false)
        builder.show()
        builder.setOnDismissListener {
            ActivityCompat.requestPermissions(
                activity,
                permissions,
                requestCode
            )
        }
    } else ActivityCompat.requestPermissions(activity,
        permissions,
        requestCode)

}

internal fun startService(context: Context, command: TrackerService.Command, options: Map<String, String> = mapOf()) {
    if (command == TrackerService.Command.START) {
        startTrackerService(context, command, Intent(context, MainActivity::class.java), options)
    } else {
        startTrackerService(context, command, null, options)
    }
}