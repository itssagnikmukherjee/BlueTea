package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes

@Composable
fun LoginScreen(viewModel: ViewModels = hiltViewModel(), navController: NavController) {
    val loginState = viewModel.loginUserState.collectAsState()
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }

    // State for showing the "Forgot Password" dialog
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    LaunchedEffect(loginState.value) {
        when {
            loginState.value.error.isNotEmpty() -> {
                Toast.makeText(context, loginState.value.error, Toast.LENGTH_SHORT).show()
            }
            loginState.value.data != null -> {
                Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.HomeScreen)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        if (loginState.value.isLoading) {
            CircularProgressIndicator()
        } else {
            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.padding(8.dp)
            )

            // Password Field
            OutlinedTextField(
                value = pass,
                onValueChange = { pass = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(8.dp)
            )

            // Login Button
            Button(
                onClick = {
                    viewModel.loginWithEmailPass(email, pass)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Login")
            }

            // Sign Up Button
            Button(
                onClick = {
                    navController.navigate(Routes.SignUpScreen)
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Sign Up")
            }

            // Forgot Password Button
            TextButton(
                onClick = {
                    showForgotPasswordDialog = true // Show the "Forgot Password" dialog
                },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Forgot Password?")
            }
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside or presses the back button
                showForgotPasswordDialog = false
            },
            title = {
                Text(text = "Forgot Password")
            },
            text = {
                Column {
                    Text("Enter your email address to reset your password.")
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Send password reset email
                        if (email.isNotEmpty()) {
                            FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(
                                            context,
                                            "Password reset email sent to $email",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Failed to send password reset email: ${task.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            showForgotPasswordDialog = false // Close the dialog
                        } else {
                            Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                        }
                    }
                ) {
                    Text("Send")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        // Dismiss the dialog
                        showForgotPasswordDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}