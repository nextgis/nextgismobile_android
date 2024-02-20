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

package com.nextgis.nextgismobile.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.nextgis.maplib.API
import com.nextgis.maplib.Constants
import com.nextgis.maplib.StatusCode
import com.nextgis.maplib.Track
import com.nextgis.maplib.TrackInfo
import com.nextgis.maplib.printMessage
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.TrackViewBinding
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone


private var continueExecution = true
private var exportTask: TrackAdapter.ExportToGPXAsyncTask? = null

@Suppress("UNUSED_PARAMETER")
fun progressCallback(status: StatusCode, complete: Double, message: String) : Boolean {
    exportTask?.publishProgress(complete, message)
    return continueExecution
}

class TrackAdapter(private val context: Context, private val tracksTable: Track) :
    RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

    private var tracks: Array<TrackInfo> = tracksTable.getTracks()

    inner class TrackViewHolder(val binding: TrackViewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackAdapter.TrackViewHolder {
        // create a new view
        val binding = TrackViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        //val view = TrackViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false) as View
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        // Reverse sort
        val newPosition = tracks.size - position - 1
        val context = holder.itemView.context
        with (holder){
            binding.trackName.text = tracks[newPosition].name
            binding.trackDescription.text = createDescription(context, tracks[newPosition].start, tracks[newPosition].stop)
            binding.shareImage.setOnClickListener {
                shareGPX(tracks[newPosition].start, tracks[newPosition].stop, tracks[newPosition].name)
            }
        }

//
//        holder.ite trackName.text = tracks[newPosition].name
//        holder.view.trackDescription.text = createDescription(context, tracks[newPosition].start, tracks[newPosition].stop)
//        holder.view.shareImage.setOnClickListener {
//            shareGPX(tracks[newPosition].start, tracks[newPosition].stop, tracks[newPosition].name)
//        }
    }

    override fun getItemCount() = tracks.size

    fun refresh() {
        tracks = tracksTable.getTracks()
        printMessage("Tracks count = ${tracks.size}")
        notifyDataSetChanged()
    }

    private fun createDescription(context: Context, start: Date, stop: Date) : String {
        val diff = stop.time - start.time
        val days = diff / Constants.millisecondsPerDay
        if(days > 0) {
            return context.getString(R.string.track_days).format(days)
        }
        val formatter = DateFormat.getTimeInstance()
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(start) + " - " + formatter.format(stop)
    }

    inner class ExportToGPXAsyncTask(private val start: Date, private val stop: Date, private val name: String) :
        AsyncTask<Void, Int, String>() {

        private var currentMessage = ""
        private var progressDialog: AlertDialog? = null

        fun publishProgress(complete: Double, message: String) {
            currentMessage = message
            publishProgress((complete * 100).toInt())
        }

        override fun onPreExecute() {
            super.onPreExecute()
            continueExecution = true
            exportTask = this
            val builder = AlertDialog.Builder(context)
            builder.setView(R.layout.progress)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel) { _, _ ->
                    continueExecution = false
                }

            progressDialog = builder.create()
            progressDialog?.show()
        }

//        fun deleteRecursive(fileOrDirectory: File?): Boolean {
//            var isOK = true
//            if (fileOrDirectory!!.isDirectory) {
//                for (child in fileOrDirectory.listFiles()) {
//                    isOK = deleteRecursive(child) && isOK
//                }
//            }
//            return fileOrDirectory.delete() && isOK
//        }

        @Synchronized
        @Throws(RuntimeException::class)
        fun createDir(dir: File) {
            if (dir.exists()) {
                return
            }
            if (!dir.mkdirs()) {
                throw RuntimeException("Can not create dir $dir")
            }
        }

        override fun doInBackground(vararg p: Void): String {

            var result = ""
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val newName = "track_${sdf.format(start)}"
            val tmp = API.getTmpDirectory()
            // Get or create share directory in tmp folder. Path to share directory is in file provider config.
            var tmpShare = tmp?.child("share")
            if(tmpShare == null) {
                tmpShare = tmp?.createDirectory("share")
            }
            if(tmpShare != null) {
                if (tracksTable.export(start, stop, newName, tmpShare, ::progressCallback)) {
                    val properties = tmpShare.getProperties()
                    val systemPath = properties["system_path"]
                    //result = "$systemPath/$newName.gpx"
                    result = "$systemPath/nga_tracks_pt.gpx"
                }
            }

            // If user click cancel, but callback not intercept it
            if(!continueExecution) {
                result = ""
            }
            return result
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)

            val progressBar : ProgressBar? = progressDialog?.findViewById<ProgressBar>(R.id.loader)

            progressBar.let {
                progressBar?.progress = values[0] ?: 0
            }
            //progressDialog?.loader?.progress = values[0] ?: 0
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            progressDialog?.dismiss()
            if(result.isEmpty()) {
                // Show error message into a toast
                Toast.makeText(context, API.lastError(), Toast.LENGTH_SHORT).show()
            }
            else {
                val gpxFile = File(result)
                val fileUri: Uri? = try {
                    FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        gpxFile)
                } catch (e: IllegalArgumentException) {
                    Log.e("tracker","The selected file can't be shared: $result")
                    Toast.makeText(context, "Error on export track", Toast.LENGTH_SHORT).show()
                    null
                }

                if(fileUri != null) {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, fileUri)
                        putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.send_track).format(name))
                        type = "text/plain"
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    }
                    context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share_gpx)))
                }
            }
            exportTask = null
        }
    }

    private fun shareGPX(start: Date, stop: Date, name: String) {
        ExportToGPXAsyncTask(start, stop, name).execute()
    }
}