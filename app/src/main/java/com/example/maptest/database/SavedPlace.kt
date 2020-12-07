package com.example.maptest.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_places_table")
data class SavedPlace(
    @PrimaryKey(autoGenerate = true)
    var saved_placeID: Long = 0L
) {
}