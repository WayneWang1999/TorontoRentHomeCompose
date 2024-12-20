package com.example.torontorenthomecompose.ui.screen.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    // Expose current user to other ViewModels
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    // User's favorite house IDs
    private val _favoriteHouseIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteHouseIds: StateFlow<Set<String>> = _favoriteHouseIds

    init {
        val user = auth.currentUser
        _isLoggedIn.value = user != null
        _userEmail.value = user?.email // Set email if user is already logged in
        if (user != null) {
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

            val user = auth.currentUser
            user?.let {
                try {
                    FirebaseFirestore.getInstance()
                        .collection("buyers")
                        .document(it.uid)
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
                    _isLoggedIn.value = true
                    _userEmail.value = email

                } else {
                    // Handle login failure (can add a state for error messages)
                    _isLoggedIn.value = false
                    _userEmail.value = null

                }
            }
    }
    // Logout function
    fun logout() {
        auth.signOut()
        _isLoggedIn.value = false
        _userEmail.value = null
    }
}
