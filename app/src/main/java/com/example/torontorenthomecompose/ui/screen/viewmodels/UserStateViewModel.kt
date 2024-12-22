package com.example.torontorenthomecompose.ui.screen.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.torontorenthomecompose.ui.screen.models.Filters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserStateViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // StateFlow to track if the user is logged in
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // StateFlow to store the user's email
    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail: StateFlow<String?> = _userEmail

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // User's favorite house IDs
    private val _favoriteHouseIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteHouseIds: StateFlow<Set<String>> = _favoriteHouseIds

    private val _filters = MutableStateFlow<Filters?>(null)
    val filters: StateFlow<Filters?> get() = _filters

    fun applyFilters(priceRange: IntRange, bedrooms: Int, bathrooms: Int, propertyType: String) {
        _filters.value = Filters(priceRange, bedrooms, bathrooms, propertyType)
    }

    // Set filters
    fun setFilters(newFilters: Filters) {
        _filters.value = newFilters
    }

    // Clear filters
    fun clearFilters() {
        _filters.value = null
    }

    init {
        val user = auth.currentUser
        _isLoggedIn.value = user != null
        _userEmail.value = user?.email
        _currentUser.value = user // Set initial user
        if (user != null) {
            Log.d("currentUser", "${user.email}")
            fetchFavorites(user.uid)
        }
    }

    private fun fetchFavorites(userId: String) {
        viewModelScope.launch {
            try {
                val document = FirebaseFirestore.getInstance()
                    .collection("buyers")
                    .document(userId)
                    .get()
                    .await()
                val favoriteIds = document["favoriteHouseIds"] as? List<String> ?: emptyList()
                _favoriteHouseIds.value = favoriteIds.toSet()
            } catch (e: Exception) {
                e.printStackTrace()
                _favoriteHouseIds.value = emptySet()
            }
        }
    }

    fun toggleFavorite(houseId: String) {
        viewModelScope.launch {
            val currentFavorites = _favoriteHouseIds.value.toMutableSet()
            if (currentFavorites.contains(houseId)) {
                currentFavorites.remove(houseId)
            } else {
                currentFavorites.add(houseId)
            }
            _favoriteHouseIds.value = currentFavorites

            _currentUser.value?.let { user ->
                try {
                    FirebaseFirestore.getInstance()
                        .collection("buyers")
                        .document(user.uid)
                        .update("favoriteHouseIds", currentFavorites.toList())
                        .await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Login function
    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    _isLoggedIn.value = true
                    _userEmail.value = user?.email
                    _currentUser.value = user // Update current user
                    _errorMessage.value = ""

                    user?.let {
                        fetchFavorites(it.uid)
                    }
                } else {
                    _isLoggedIn.value = false
                    _userEmail.value = null
                    _currentUser.value = null // Clear current user
                    _errorMessage.value = "Email or Password not correct!!!"
                }
            }
    }

    // Logout function
    fun logout() {
        auth.signOut()
        _isLoggedIn.value = false
        _userEmail.value = null
        _currentUser.value = null // Clear current user
        _favoriteHouseIds.value = emptySet() // Clear favorites
        _filters.value = null // Clear filters if applicable
    }
}

