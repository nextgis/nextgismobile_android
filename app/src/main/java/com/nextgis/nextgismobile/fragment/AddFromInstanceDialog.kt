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

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.activity.AddInstanceActivity
import com.nextgis.nextgismobile.adapter.OnInstanceClickListener
import com.nextgis.nextgismobile.data.Instance
import com.nextgis.nextgismobile.databinding.DialogAddFromInstanceBinding
import com.pawegio.kandroid.IntentFor


class AddFromInstanceDialog : DialogFragment(), OnInstanceClickListener {
    override fun onInstanceClick(instance: Instance) {
        fragment?.createFromInstance(instance)
        dismiss()
    }

    private lateinit var binding: DialogAddFromInstanceBinding
    private var fragment: LayersFragment? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), R.style.Base_ThemeOverlay_AppCompat_Dialog_Alert)
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_add_from_instance, null, false)
        dialog.setContentView(binding.root)
        binding.fragment = this
        binding.list.adapter = getInstances(this, false)
        binding.list.layoutManager = LinearLayoutManager(requireActivity(), RecyclerView.VERTICAL, false)
        return dialog
    }

    fun show(fragment: LayersFragment) {
        this.fragment = fragment
        fragment.activity?.let {
            show(it.supportFragmentManager, TAG)
        }
    }

    fun addAccount() {
        context?.let { startActivityForResult(IntentFor<AddInstanceActivity>(it), AddInstanceActivity.ADD_INSTANCE_REQUEST) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            AddInstanceActivity.ADD_INSTANCE_REQUEST -> {
                if (resultCode == Activity.RESULT_OK)
                    replaceInstances(binding.list.adapter)
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        const val TAG = "AddFromInstanceDialog"
    }
}