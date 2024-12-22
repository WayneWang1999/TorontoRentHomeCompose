package com.example.torontorenthome.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.torontorenthome.models.House
import com.example.torontorenthomecompose.ui.screen.models.Converters

@Database(entities = [House::class], version = 1)
@TypeConverters(Converters::class) // Register the converter
abstract class HouseDatabase : RoomDatabase() {
    abstract fun houseDao(): HouseDao
}