package com.ionix.demontir.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import com.google.android.gms.location.*

class LocationLiveData(private val context: Context) : LiveData<LocationData>() {
    private val _fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationRequest = LocationRequest.create().apply {
        interval = 3000
        fastestInterval = 2000
        priority = Priority.PRIORITY_HIGH_ACCURACY
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)

            p0.lastLocation?.let {
                val longitude = String.format("%.3f", it.longitude).replace(",",".").toDouble()
                val latitude = String.format("%.3f", it.latitude).replace(",", ".").toDouble()

                value = LocationData(
                    longitude = longitude,
                    latitude = latitude
                )
            }
        }
    }

    fun startLocationLiveUpdate(){
        /**Request Update of Location**/
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        _fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationLiveUpdate(){
        _fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onActive() {
        super.onActive()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        /**Get Location First Time**/
        _fusedLocationClient.lastLocation.addOnSuccessListener {
            value = LocationData(
                longitude = it.longitude,
                latitude = it.latitude
            )
        }

        /**Request Live Location**/
        startLocationLiveUpdate()
    }

    override fun onInactive() {
        super.onInactive()

        /**Remove update of location when livedata is not being observed anymore**/
        stopLocationLiveUpdate()
    }
}