package com.ibrahimaluc.busbookingapp.ui.screen.map

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.ibrahimaluc.busbookingapp.R
import com.ibrahimaluc.busbookingapp.core.extensions.collectLatestLifecycleFlow
import com.ibrahimaluc.busbookingapp.data.remote.MapItem
import com.ibrahimaluc.busbookingapp.databinding.ActivityMapsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var _binding: ActivityMapsBinding
    private val binding get() = _binding

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    var trackBoolean: Boolean? = null

    private var stationList: List<MapItem>? = emptyList()
    private val viewModel: MapViewModel by lazy {
        ViewModelProvider(this)[MapViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        registerLauncher()

        sharedPreferences = this.getSharedPreferences("com.ibrahimaluc.busbookingapp", MODE_PRIVATE)
        trackBoolean = false

        collectLatestLifecycleFlow(viewModel.state, ::handleMapViewState)
        viewModel.getMapList(6)
    }

    private fun handleMapViewState(mapsUiState: MapsUiState) {
        stationList = mapsUiState.station
        Log.d("map", "stations: $stationList")
        stationList?.let { updateMapWithStations(it) }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trackBoolean = sharedPreferences.getBoolean("trackBoolean", false)
                if (!trackBoolean!!) {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 3f))
                    sharedPreferences.edit().putBoolean("trackBoolean", true).apply()
                }
            }
        }
        //ContextCompat for previous version compatibility
        if (ContextCompat.checkSelfPermission(
                this@MapsActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                Snackbar.make(
                    binding.root,
                    "Permission needed for location",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Give Permission") {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            } else {
                //request permission
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        else {
            //permission granted
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener
            )
            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation != null) {
                val lastUserLocation = LatLng(lastLocation.latitude, lastLocation.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 3f))
                mMap.isMyLocationEnabled = true
            }
        }
    }

    private fun registerLauncher() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    //permission granted
                    if (ContextCompat.checkSelfPermission(
                            this@MapsActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            0,
                            0f,
                            locationListener
                        )
                        val lastLocation =
                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                        if (lastLocation != null) {
                            val lastUserLocation =
                                LatLng(lastLocation.latitude, lastLocation.longitude)
                            mMap.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    lastUserLocation,
                                    15f
                                )
                            )
                        }
                        mMap.isMyLocationEnabled = true
                    }
                } else {
                    //permission denied
                    Toast.makeText(this@MapsActivity, "Permisson needed!", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun updateMapWithStations(stations: List<MapItem>) {
        for (station in stations) {
            station.centerCoordinates?.let { coordinates ->
                val latLong = coordinates.split(",").runCatching {
                    val lat = getOrNull(0)?.toDouble()
                    val long = getOrNull(1)?.toDouble()
                    Pair(lat, long)
                }.getOrNull()

                if ((latLong?.first != null) && (latLong.second != null)) {
                    val markerOptions = MarkerOptions()
                        .position(LatLng(latLong.first!!, latLong.second!!))
                        .title("${station.tripsCount} Trips")
                    mMap.addMarker(markerOptions)
                    binding.isActive=true

//                    val circleOptions = CircleOptions()
//                        .center(LatLng(latLong.first!!, latLong.second!!))
//                        .radius(200.0)
//                        .strokeWidth(2f)
//                        .strokeColor(Color.RED)
//                        .fillColor(Color.argb(70, 0, 0, 255))
//                    mMap.addCircle(circleOptions)
//
//                    Log.d("Map", "Circle added at: ${LatLng(latLong.first!!, latLong.second!!)}")

                } else {
                    Log.e("Map", "Invalid coordinates format: $coordinates")
                }
            }
        }
    }
}