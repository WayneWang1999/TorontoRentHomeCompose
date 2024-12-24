package com.example.torontorenthomecompose.data

import com.example.torontorenthomecompose.ui.screen.viewmodels.UserStateViewModel
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

    @Module
    @InstallIn(SingletonComponent::class)
    object ViewModelModule {

        @Provides
        @Singleton
        fun provideUserStateViewModel(): UserStateViewModel {
            return UserStateViewModel()
        }
    }
}