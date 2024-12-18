package com.example.torontorenthomecompose.ui.screen.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AccountScreenViewModel : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    // UI State
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val isPasswordVisible = mutableStateOf(false)
    val isLoggedIn = mutableStateOf(firebaseAuth.currentUser != null)
    val errorMessage = mutableStateOf<String?>(null)
    val userEmail = mutableStateOf(firebaseAuth.currentUser?.email ?: "")

    // Login Function
    fun loginUser() {
        if (email.value.isBlank() || password.value.length < 6) {
            errorMessage.value = "Invalid email or password"
            return
        }

        firebaseAuth.signInWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    isLoggedIn.value = true
                    userEmail.value = firebaseAuth.currentUser?.email ?: "User"
                    errorMessage.value = null
                    Log.d("Login state", "login successful")
                } else {
                    errorMessage.value = "Login failed. Check your credentials."
                    Log.d("Login state", "login failure")
                }
            }
    }

    // Logout Function
    fun logoutUser() {
        firebaseAuth.signOut()
        isLoggedIn.value = false
        userEmail.value = ""
        email.value = ""
        password.value = ""
        errorMessage.value = null
    }
}