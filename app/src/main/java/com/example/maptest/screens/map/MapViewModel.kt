package com.example.maptest.screens.map

import android.app.Application
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.maptest.database.SavedPlaceDao
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.net.PlacesClient

class MapViewModel(
        val database: SavedPlaceDao,
        application: Application,
        private val mFusedLocationProviderClient: FusedLocationProviderClient,
        private val placesClient: PlacesClient
) : AndroidViewModel(application) {

        companion object{
                private const val TAG : String = "MAP_TEST_DEBUG"
        }

        init{
                getLastKnownLocation()
        }

        private var _currentLocation = MutableLiveData<Location>()
        val currentLocation : LiveData<Location>
                get() = _currentLocation


        private fun getLastKnownLocation(){
        /*
        * Get the best and most recent location of the device, which may be null in rare
        * cases when a location is not available.
        */
                try {
                        val locationResult: Task<*> =
                                mFusedLocationProviderClient.lastLocation
                        locationResult.addOnCompleteListener {
                                if (it.isSuccessful){
                                        Log.d(TAG, it.result.toString())
                                        _currentLocation.value = it.result as Location
                                }
                                else{
                                        Log.d(TAG, it.result.toString())
                                }
                        }
                } catch (e: SecurityException) {
                        Log.e(TAG, e.message.toString())
                }
        }
}


