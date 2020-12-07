package com.example.maptest.screens.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.maptest.R
import com.example.maptest.database.SavedPlaceDatabase
import com.example.maptest.databinding.FragmentMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places


class MapFragment : Fragment() {

    companion object{
        private const val TAG : String = "MAP_TEST_DEBUG"
        private const val DEFAULT_ZOOM = 18f
        private val DEFAULT_LOCATION = LatLng(-34.0, 151.0)
    }

    private var mLocationPermissionGranted: Boolean = false

    private lateinit var mGoogleMap: GoogleMap

    private val callback = OnMapReadyCallback { googleMap ->

        mGoogleMap = googleMap
        updateLocationUI()
        setMapViewSettings()
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this.requireContext(), R.raw.map_style_dark))
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = DataBindingUtil.inflate<FragmentMapBinding>(inflater, R.layout.fragment_map, container, false)

        getLocationPermission()

        val application = requireNotNull(this.activity).application

        val dataSource = SavedPlaceDatabase.getInstance(application).savedPlaceDao

        val mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.requireContext())

        Places.initialize(application.applicationContext, getString(R.string.google_maps_key))

        val placesClient = Places.createClient(this.requireContext())

        val viewModelFactory = MapViewModelFactory(dataSource, application, mFusedLocationProviderClient, placesClient)

        val viewModel = ViewModelProvider(this, viewModelFactory).get(MapViewModel::class.java)

        binding.mapViewModel = viewModel

        binding.lifecycleOwner = this

        if (mLocationPermissionGranted) {
            viewModel.currentLocation.observe(viewLifecycleOwner, Observer {
                if (it != null){
                    Log.d(TAG, it.toString())
                    mGoogleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                it.latitude,
                                it.longitude
                            ), DEFAULT_ZOOM
                        )
                    )
                }
                else{
                    mGoogleMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            DEFAULT_LOCATION,
                            DEFAULT_ZOOM
                        )
                    )
                    mGoogleMap.uiSettings.isMyLocationButtonEnabled = false
                    Log.d(TAG, "Current location is null. Using defaults.")
                }
            })
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    private fun getLocationPermission() {
        /*
        * Request location permission, so that we can get the location of the
        * device. The result of the permission request is handled by a callback,
        * onRequestPermissionsResult.
        */
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this.requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        mLocationPermissionGranted = false
        when (requestCode) {
            0 -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true
                }
            }
        }
        updateLocationUI()
    }

    private fun updateLocationUI() {
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.isMyLocationEnabled = true
                mGoogleMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                mGoogleMap.isMyLocationEnabled = false
                mGoogleMap.uiSettings.isMyLocationButtonEnabled = false
                getLocationPermission()
            }
        } catch (e: SecurityException) {
            Log.d(TAG, e.message!!)
        }
    }


    private fun setMapViewSettings(){
        mGoogleMap.let{
            it.isTrafficEnabled = true
            it.uiSettings.isZoomControlsEnabled = true
            it.isBuildingsEnabled = true
            it.setMinZoomPreference(10f)
            it.setMaxZoomPreference(20f)
        }
    }

}