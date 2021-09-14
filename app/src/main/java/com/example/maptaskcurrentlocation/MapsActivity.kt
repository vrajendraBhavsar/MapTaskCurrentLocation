package com.example.maptaskcurrentlocation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import androidx.lifecycle.lifecycleScope

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.maptaskcurrentlocation.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.Marker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    @Inject
    lateinit var currentLocation: CurrentLocation

    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMap: GoogleMap
    var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            currentLocation.init(this@MapsActivity)
        }
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        requestLocation.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.setOnMarkerClickListener(this)
        setUpMap()
    }

    var requestLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){
        it.filterValues {
            it.not()
        }.isEmpty()
    }

    private fun setUpMap() {
        location = currentLocation.getCurrentLocation()
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return
//        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestMultiplePermissions.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION ))
            return
        }
        mMap.isMyLocationEnabled = true
        /*if (currentLocation.equals(null)){
            currentLocation.getCurrentLocation(this)
        }*/
        location?.let {
            val currentLocationLatLng = LatLng(it.latitude, it.longitude)
            placeMarkerOnMap(currentLocationLatLng)
        }
    }

    private fun placeMarkerOnMap(lastLocationLatLng: LatLng) {
        var markerOptions = MarkerOptions().position(lastLocationLatLng)
        markerOptions.title("${lastLocationLatLng.latitude}, ${lastLocationLatLng.longitude}")
        mMap.addMarker(markerOptions)
        Toast.makeText(this, "${lastLocationLatLng.latitude}, ${lastLocationLatLng.longitude}", Toast.LENGTH_SHORT).show()
    }

    override fun onMarkerClick(p0: Marker): Boolean = false

    //callback to get result ..Observing permission change
    private var requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){
        it.filterValues { it.not() }.isEmpty()    //it.not() -> will take keys/String that has false val
        //this will be empty when user accepts all permissions.
    }
}