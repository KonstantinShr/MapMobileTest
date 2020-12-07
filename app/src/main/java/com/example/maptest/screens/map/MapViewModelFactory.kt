package com.example.maptest.screens.map

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.maptest.database.SavedPlaceDao
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.libraries.places.api.net.PlacesClient
import java.lang.IllegalArgumentException

class MapViewModelFactory(private val database: SavedPlaceDao,
                          private val application: Application,
                          private val mFusedLocationProviderClient: FusedLocationProviderClient,
                          private val placesClient: PlacesClient)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapViewModel::class.java)){
            return MapViewModel(database, application, mFusedLocationProviderClient, placesClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}