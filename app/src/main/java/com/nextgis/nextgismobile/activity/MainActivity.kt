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

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.nextgis.maplib.CoordinateTransformation
import com.nextgis.maplib.GestureDelegate
import com.nextgis.maplib.Location
import com.nextgis.maplib.MapDocument
import com.nextgis.maplib.Point
import com.nextgis.maplib.service.TrackerDelegate
import com.nextgis.maplib.service.TrackerService
import com.nextgis.nextgismobile.R
import com.nextgis.nextgismobile.databinding.ActivityMainBinding
import com.nextgis.nextgismobile.fragment.LayersFragment
import com.nextgis.nextgismobile.fragment.LocationInfoFragment
import com.nextgis.nextgismobile.fragment.TracksFragment
import com.nextgis.nextgismobile.util.dpToPx
import com.nextgis.nextgismobile.util.requestPermissionsUtil
import com.nextgis.nextgismobile.util.statusBarHeight
import com.nextgis.nextgismobile.util.tint
import com.nextgis.nextgismobile.viewmodel.LocationViewModel
import com.nextgis.nextgismobile.viewmodel.MapViewModel
import com.nextgis.nextgismobile.viewmodel.SettingsViewModel
import com.nextgis.nextgismobile.util.startService
import com.pawegio.kandroid.runDelayed
import com.pawegio.kandroid.startActivity
import com.pawegio.kandroid.toast
import java.util.Date

const val LOCATION_REQUEST = 2
class MainActivity : BaseActivity(), GestureDelegate {

    private lateinit var binding: ActivityMainBinding
    internal var map: MapDocument? = null



    private var mIsServiceRunning = false


    private val mTrackerDelegate = object : TrackerDelegate {
        override fun onLocationChanged(location: Location) {
        }

        override fun onStatusChanged(status: TrackerService.Status, trackName: String, trackStartTime: Date) {
            mIsServiceRunning = status == TrackerService.Status.RUNNING
            updateServiceStatus()
        }
    }

    private fun updateServiceStatus() {
        if (mIsServiceRunning) {
            //binding.fab.setImageResource(R.drawable.ic_pause)
        }
        else {
            //binding.fab.setImageResource(R.drawable.ic_play)
        }
        //binding.contentMain.tracksGroup.text = getString(R.string.tracks) + " (${mTracksAdapter?.itemCount})"
    }

    private var mTrackerService: TrackerService? = null
    private var mIsBound = false
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as TrackerService.LocalBinder
            mTrackerService = binder.getService()
            mTrackerService?.addDelegate(mTrackerDelegate)
            mIsBound = true
            mTrackerService?.status()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mIsBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.bottomBar)

        initMap()
        initLocation()

        binding.apply {
            activity = this@MainActivity
            val params = card.layoutParams as CoordinatorLayout.LayoutParams
            params.topMargin = statusBarHeight + dpToPx(8)

            binding.fabAdd.tint(R.color.colorButton)

            binding.addNewGeometry.setOnClickListener {
                fabAdd.collapse()
                snackbar("addNewGeometry")
            }

            binding.addGeometryByWalk.setOnClickListener {
                fabAdd.collapse()
                snackbar("addGeometryByWalk")
            }

            zoomIn.setOnClickListener { binding.mapinclude.mapView.zoomIn() }
            zoomOut.setOnClickListener { binding.mapinclude.mapView.zoomOut() }
            locate.setOnClickListener {
                val locationModel = ViewModelProvider(this@MainActivity).get(LocationViewModel::class.java)
                locationModel.location?.let {
                    val ct = CoordinateTransformation.new(4326, 3857)
                    val newCenter = ct.transform(Point(it.longitude, it.latitude))
                    binding.mapinclude.mapView.centerMap(newCenter)
                }
            }

            val settingsModel = ViewModelProvider(this@MainActivity).get(SettingsViewModel::class.java)
            settingsModel.setup(this@MainActivity)
            settings = settingsModel
        }
        binding.executePendingBindings()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
//            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//        getWindow().setStatusBarColor(getColor(R.color.whiteAlpha));
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_GEO)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_GEO -> {
                if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    val fragment = supportFragmentManager.findFragmentByTag(LOCATION_INFO) as? LocationInfoFragment
                    fragment?.onStart()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun initLocation() {
        val location = LocationInfoFragment()
        supportFragmentManager.beginTransaction().replace(R.id.location_info, location, LOCATION_INFO).commitAllowingStateLoss()
    }

    private fun initMap(){
        val mapModel = ViewModelProvider(this).get(MapViewModel::class.java)
        mapModel.init(this)

        map = mapModel.load(this)
        map?.let {
            binding.mapinclude.mapView.setMap(it)
            binding.mapinclude.mapView.showLocation = true

        }

        binding.mapinclude.mapView.registerGestureRecognizers(this)
        binding.mapinclude.mapView.freeze = false
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, TrackerService::class.java)
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
        runDelayed(1500) { binding.settings?.load() }
    }

    override fun onStop() {
        super.onStop()
        if(mIsBound) {
            mTrackerService?.removeDelegate(mTrackerDelegate)
            unbindService(mServiceConnection)
        }
        map?.save()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity<SettingsActivity>()
                true
            }
            R.id.menu_favorites -> {
                toast(R.string.use_favorites)
                true
            }
            R.id.menu_share_map -> {
                toast(R.string.share_map)
                true
            }
            R.id.menu_pointing -> {
                toast(R.string.pointing)
                true
            }

            R.id.menu_tracks_list -> {
                tracks()
                true
            }

            R.id.menu_track_record -> {
                if(mIsServiceRunning) {
                    toast(R.string.track_record_stop)
                    startService(this, TrackerService.Command.STOP)
                }
                else {

                    val activity = this

                    TrackerService.showBackgroundDialog(this, object : TrackerService.BackgroundPermissionCallback {
                        override fun beforeAndroid10(hasBackgroundPermission: Boolean) {
                            if (!hasBackgroundPermission) {
                                val permissions = arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION
                                )
                                requestPermissionsUtil(R.string.permissions_x,
                                    R.string.requested_permissions,
                                    LOCATION_REQUEST,
                                    permissions,
                                    activity)
                            } else {
                                toast(R.string.track_record_start)
                                startService(baseContext, TrackerService.Command.START)
                                if(!mIsBound) {
                                    val intent = Intent(baseContext, TrackerService::class.java)
                                    bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
                                }                         }
                        }

                        @RequiresApi(api = Build.VERSION_CODES.Q)
                        override fun onAndroid10(hasBackgroundPermission: Boolean) {
                            if (!hasBackgroundPermission) {
                                requestPermissions()
                            } else {
                                toast(R.string.track_record_start)
                                startService(baseContext, TrackerService.Command.START)
                                if(!mIsBound) {
                                    val intent = Intent(baseContext, TrackerService::class.java)
                                    bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
                                }                        }
                        }

                        @RequiresApi(api = Build.VERSION_CODES.Q)
                        override fun afterAndroid10(hasBackgroundPermission: Boolean) {
                            if (!hasBackgroundPermission) {
                                requestPermissions()
                            } else {
                                toast(R.string.track_record_start)
                                startService(baseContext, TrackerService.Command.START)
                                if(!mIsBound) {
                                    val intent = Intent(baseContext, TrackerService::class.java)
                                    bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE)
                                }                        }
                        }
                    })

                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun requestPermissions() {
        val permissions = arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        ActivityCompat.requestPermissions(this, permissions, LOCATION_REQUEST)
    }

    fun layers() {
        val layers = LayersFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, layers).addToBackStack("layers").commitAllowingStateLoss()
    }


    fun tracks() {
        val layers = TracksFragment()
        supportFragmentManager.beginTransaction().replace(R.id.container, layers).addToBackStack("tracks").commitAllowingStateLoss()
    }


    fun search() {
        toast(R.string.not_implemented)
    }

    fun refresh() {
//        mapView.refresh()
        binding.mapinclude.mapView.invalidate(binding.mapinclude.mapView.mapExtent)
    }

    fun snackbar(@StringRes text: Int) {
        val snackbar = Snackbar.make(binding.coordinator, text, Snackbar.LENGTH_LONG).setAction("Action", null).setAnchorView(R.id.bottomBar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            snackbar.view.elevation = dpToPx(8).toFloat()
        }
        snackbar.show()
    }

    fun snackbar(text: String) {
        val snackbar = Snackbar.make(binding.coordinator, text, Snackbar.LENGTH_LONG).setAction("Action", null).setAnchorView(R.id.bottomBar)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            snackbar.view.elevation = dpToPx(8).toFloat()
        }
        snackbar.show()
    }

    companion object {
        const val REQUEST_GEO = 630
        const val LOCATION_INFO = "LOCATION_INFO"
    }
}
