package com.ibrahimaluc.busbookingapp.ui.screen.map

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.ibrahimaluc.busbookingapp.R
import com.ibrahimaluc.busbookingapp.core.base.BaseFragment
import com.ibrahimaluc.busbookingapp.core.extensions.collectLatestLifecycleFlow
import com.ibrahimaluc.busbookingapp.data.remote.MapItem
import com.ibrahimaluc.busbookingapp.databinding.FragmentMapsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsFragment : BaseFragment<MapViewModel, FragmentMapsBinding>(
    MapViewModel::class.java,
    FragmentMapsBinding::inflate
), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    var trackBoolean: Boolean? = null
    private var stationList: List<MapItem>? = emptyList()
    private var selectedStation: MapItem? = null
    private var selectedMarker: Marker? = null
    private val args: MapsFragmentArgs by navArgs()

    override fun onCreateViewInvoke() {
        collectLatestLifecycleFlow(viewModel.state, ::handleMapViewState)
        viewModel.getMapList(6)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        registerLauncher()
        sharedPreferences = requireActivity().getSharedPreferences(
            "com.ibrahimaluc.busbookingapp",
            AppCompatActivity.MODE_PRIVATE
        )
        trackBoolean = false
    }

    private fun handleMapViewState(mapsUiState: MapsUiState) {
        setProgressStatus(mapsUiState.isLoading)
        mapsUiState.station?.let {
            updateMapWithStations(it)
            Log.d("map", "stations: $stationList")
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        locationManager =
            requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trackBoolean = sharedPreferences.getBoolean("trackBoolean", false)
                if (!trackBoolean!!) {
                    val userLocation = LatLng(location.latitude, location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 12f))

                    sharedPreferences.edit().putBoolean("trackBoolean", true).apply()
                }
            }
        }
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        )
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastUserLocation, 12f))
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
                            requireContext(),
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
                                    12f
                                )
                            )
                        }
                        mMap.isMyLocationEnabled = true
                    }
                } else {
                    //permission denied
                    Toast.makeText(requireContext(), "Permisson needed!", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun updateMapWithStations(stations: List<MapItem>) {
        mMap.clear()
        val markerOptions = MarkerOptions()
        for (station in stations) {
            station.centerCoordinates?.let { coordinates ->
                val latLong = coordinates.split(",").runCatching {
                    val lat = getOrNull(0)?.toDouble()
                    val long = getOrNull(1)?.toDouble()
                    Pair(lat, long)
                }.getOrNull()
                if ((latLong?.first != null) && (latLong.second != null)) {
                    markerOptions.position(LatLng(latLong.first!!, latLong.second!!))
                        .title("${station.tripsCount} Trips")

                    if (checkBookedStation(station)) {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_custom_pin_completed))
                    } else {
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_custom_pin))
                    }
                    val marker = mMap.addMarker(markerOptions)
                    marker?.tag = station
                } else {
                    Log.e("Map", "Invalid coordinates format: $coordinates")
                }
            }
        }
        setOnMarkerClickListener()
    }

    private fun setOnMarkerClickListener() {
        mMap.setOnMarkerClickListener { marker ->
            selectedMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_custom_pin))

            selectedMarker = marker
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_custom_pin_selected))

            binding.isActive = true
            selectedStation = marker.tag as? MapItem
            false
        }
        binding.btListTrips.setOnClickListener {
            selectedStation?.let {
                navigateToTripListFragment(it)
            }
        }
    }

    private fun checkBookedStation(station: MapItem): Boolean {
        return try {
            val bookedStation: MapItem? = args.bookedSt
            (bookedStation != null) && (bookedStation.id == station.id)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun navigateToTripListFragment(tripList: MapItem) {
        val action = MapsFragmentDirections.actionMapsFragmentToListFragment(tripList)
        findNavController().navigate(action)
    }
}