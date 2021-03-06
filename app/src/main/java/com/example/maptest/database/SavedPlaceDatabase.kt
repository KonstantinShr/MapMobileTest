package com.example.maptest.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [SavedPlace::class], version = 1, exportSchema = false)
abstract class SavedPlaceDatabase : RoomDatabase() {

    abstract val savedPlaceDao:SavedPlaceDao

    /**
     * Define a companion object, this allows us to add functions on the SleepDatabase class.
     *
     * For example, clients can call `SleepDatabase.getInstance(context)` to instantiate
     * a new SleepDatabase.
     */
    companion object{
        /**
         * INSTANCE will keep a reference to any database returned via getInstance.
         *
         * This will help us avoid repeatedly initializing the database, which is expensive.
         *
         *  The value of a volatile variable will never be cached, and all writes and
         *  reads will be done to and from the main memory. It means that changes made by one
         *  thread to shared data are visible to other threads.
         */
        @Volatile
        private var INSTANCE : SavedPlaceDatabase? = null

        fun getInstance(context: Context) : SavedPlaceDatabase{
            var instance = INSTANCE

            if (instance == null){
                instance = Room.databaseBuilder(context.applicationContext,
                    SavedPlaceDatabase::class.java,
                    "saved_places_database")
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
            }
            return instance
        }
    }
}