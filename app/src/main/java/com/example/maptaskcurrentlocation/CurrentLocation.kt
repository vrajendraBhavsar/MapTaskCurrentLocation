package com.example.maptaskcurrentlocation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
class CurrentLocation() {
    private var hasGps: Boolean = false
    private var lastLocation: Location? = null
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null


    suspend fun init(activity: Activity){
        if (isGpsEnabled(activity)) {
            if (fusedLocationProviderClient == null) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
                    lastLocation = fusedLocationProviderClient?.lastLocation?.await()
            }
        } else {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    101
                )
            }
        }
    }

    fun getCurrentLocation(): Location? {
        return lastLocation
    }

    private fun isGpsEnabled(activity: Activity): Boolean {
//        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) //getting Gps State
        return true
    }
}