package com.example.torontorenthomecompose.ui.screen


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.torontorenthomecompose.ui.screen.viewmodels.AccountScreenViewModel
import com.example.torontorenthomecompose.ui.screen.viewmodels.FavoriteScreenViewModel

@Composable
fun AccountScreen(accountScreenViewModel: AccountScreenViewModel = viewModel()) {
    // ViewModel State
    val favoriteScreenViewModel: FavoriteScreenViewModel = viewModel()
    val email by accountScreenViewModel.email
    val password by accountScreenViewModel.password
    val isPasswordVisible by accountScreenViewModel.isPasswordVisible
    val isLoggedIn by accountScreenViewModel.isLoggedIn
    val errorMessage by accountScreenViewModel.errorMessage
    val userEmail by accountScreenViewModel.userEmail

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        if (!isLoggedIn) {
            // Email Input
            OutlinedTextField(
                value = email,
                onValueChange = { accountScreenViewModel.email.value = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Password Input
            OutlinedTextField(
                value = password,
                onValueChange = { accountScreenViewModel.password.value = it },
                label = { Text("Password") },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { accountScreenViewModel.isPasswordVisible.value = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Error Message
            if (errorMessage != null) {
                Text(
                    text = errorMessage?:"",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Login Button
            Button(
                onClick = { accountScreenViewModel.loginUser() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Login")
            }
        } else {
            // Logged-In UI
            Text(
                text = "Welcome, $userEmail!",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    accountScreenViewModel.logoutUser()
                    favoriteScreenViewModel.logout() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}
