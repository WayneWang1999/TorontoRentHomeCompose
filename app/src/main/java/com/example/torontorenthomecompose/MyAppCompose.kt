package com.example.torontorenthomecompose

import android.app.Application
import androidx.room.Room
import com.example.torontorenthome.data.HouseDatabase
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyAppCompose : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}