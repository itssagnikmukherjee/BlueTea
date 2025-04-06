package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.itssagnikmukherjee.blueteauser.R
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors
import com.itssagnikmukherjee.blueteauser.presentation.theme.fontFamily

@Composable
fun LoginScreen(viewModel: ViewModels = hiltViewModel(), navController: NavController) {
    val loginState = viewModel.loginUserState.collectAsState()
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                if (loginState.value.isLoading) {
                    ShimmerScreen()
                } else {
                    val outlinedTextFieldStyle = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CustomColors.darkGray,
                        unfocusedBorderColor = CustomColors.darkGray,
                        focusedLabelColor = CustomColors.darkGray,
                        unfocusedLabelColor = CustomColors.darkGray,
                        cursorColor = CustomColors.darkGray,
                        focusedTextColor = CustomColors.darkGray,
                        unfocusedTextColor = CustomColors.primaryBlack,
                    )
                    Text(
                        text = "Login",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 26.sp,
                        modifier = Modifier.padding(bottom = 32.dp),
                        fontFamily = fontFamily
                    )
                    OutlinedTextField(
                        value = email,
                        colors = outlinedTextFieldStyle,
                        onValueChange = { email = it },
                        label = { Text("Email", fontFamily = fontFamily) },
                        shape = RoundedCornerShape(15.dp),
                        textStyle = TextStyle.Default.copy(fontFamily = fontFamily),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    var passwordVisibility by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = password,
                        colors = outlinedTextFieldStyle,
                        onValueChange = { password = it },
                        singleLine = true,
                        trailingIcon = {
                            Icon(
                                imageVector = if (passwordVisibility) {
                                    Icons.Default.Visibility
                                } else {
                                    Icons.Default.VisibilityOff
                                },
                                contentDescription = "Toggle Password Visibility",
                                modifier = Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ){
                                    passwordVisibility = !passwordVisibility
                                }
                            )
                        },
                        label = { Text("Password", fontFamily = fontFamily) },
                        shape = RoundedCornerShape(15.dp),
                        textStyle = TextStyle.Default.copy(fontFamily = fontFamily),
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (passwordVisibility) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        }
                    )


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                            Text(
                                text = "forgot password",
                                color = CustomColors.darkGray,
                                modifier = Modifier.clickable{
                                    showForgotPasswordDialog = true
                                },
                                textDecoration = TextDecoration.Underline,
                                fontFamily = fontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "don't have account ?",
                                color = CustomColors.darkGray,
                                modifier = Modifier.clickable{
                                    navController.navigate(Routes.SignUpScreen)
                                },
                                textDecoration = TextDecoration.Underline,
                                fontFamily = fontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login Button
                    Button(
                        onClick = {
                            if(email.isNotEmpty() && password.isNotEmpty()) {
                                if(!email.contains("@") && !email.contains(".com") && password.length < 6){
                                    Toast.makeText(context, "Incorrect Format", Toast.LENGTH_SHORT).show()
                                }
                                viewModel.loginWithEmailPass(email, password)
                            }
                            else {
                                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CustomColors.primaryBlack
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Login", fontFamily = fontFamily, color = Color.White, fontWeight = FontWeight.Normal)
                    }

                    Spacer(modifier = Modifier.height(8.dp))


                    OutlinedButton(
                        onClick = {
                            navController.navigate(Routes.SignUpScreen)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black,
                        ),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(3.dp, CustomColors.primaryBlack),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Signup", fontFamily = fontFamily, fontWeight = FontWeight.Normal, color = CustomColors.primaryBlack)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp, horizontal = 60.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Divider(
                            color = Color.Gray,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))


                    OutlinedButton(
                        onClick = { /* Google Sign in implementation */ },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(3.dp, CustomColors.primaryBlack),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("Continue with ", fontFamily = fontFamily, fontWeight = FontWeight.Medium, color = CustomColors.primaryBlack)
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(painter = painterResource(R.drawable.google),"", modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { /* Meta Sign in implementation */ },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(3.dp, CustomColors.primaryBlack),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // In production app, use an actual Meta icon here
                            Text("Continue with ", fontFamily = fontFamily, fontWeight = FontWeight.Medium, color = CustomColors.primaryBlack)
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(painter = painterResource(R.drawable.meta),"", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }

        // Forgot Password Dialog
        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = {
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
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
                                showForgotPasswordDialog = false
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please enter your email",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    ) {
                        Text("Send")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showForgotPasswordDialog = false
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}