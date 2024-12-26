package com.example.torontorenthomecompose.data

import android.content.Context
import androidx.room.Room
import com.example.torontorenthome.data.HouseDao
import com.example.torontorenthome.data.HouseDatabase
import com.example.torontorenthomecompose.ui.screen.viewmodels.UserStateViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)  // This makes it available across the whole app
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()  // Provide FirebaseFirestore instance
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HouseDatabase {
        return Room.databaseBuilder(
            context,
            HouseDatabase::class.java,
            "app_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideHouseDao(db: HouseDatabase): HouseDao {
        return db.houseDao()
    }
}