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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
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
import com.ibrahimaluc.busbookingapp.ui.screen.trip.ListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsActivity : AppCompatActivity() {

    private lateinit var _binding: ActivityMapsBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.findFragmentById(R.id.navHostFragmentContainer) as NavHostFragment
    }
}