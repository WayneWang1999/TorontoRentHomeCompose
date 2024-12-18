package com.example.torontorenthomecompose

import android.app.Application
import androidx.room.Room
import com.example.torontorenthome.data.HouseDatabase
import com.google.firebase.FirebaseApp

class MyAppCompose : Application() {
    lateinit var database: HouseDatabase
        private set


    override fun onCreate() {
        super.onCreate()
      //  instance=this
        database = Room.databaseBuilder(
            applicationContext,
            HouseDatabase::class.java,
            "app_database"
        ).build()
        FirebaseApp.initializeApp(this) // Initialize Firebase
    }
}