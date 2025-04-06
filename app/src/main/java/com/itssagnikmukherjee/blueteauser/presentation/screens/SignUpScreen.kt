package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.graphics.Outline
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.itssagnikmukherjee.blueteauser.R
import com.itssagnikmukherjee.blueteauser.domain.models.UserData
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors
import com.itssagnikmukherjee.blueteauser.presentation.theme.fontFamily
import java.time.format.TextStyle


@Composable
fun SignUpScreen(viewModel: ViewModels = hiltViewModel(), navController: NavController) {
    val registrationState = viewModel.registerUserState.collectAsState()
    val context = LocalContext.current

    var userFirstName by remember { mutableStateOf("") }
    var userLastName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userAddress by remember { mutableStateOf("") }
    var userPhoneNo by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var userConfirmPassword by remember { mutableStateOf("") }
    var userImage by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        userImage = uri
    }

    LaunchedEffect(registrationState.value) {
        when {
            registrationState.value.error.isNotEmpty() -> {
                Toast.makeText(context, registrationState.value.error, Toast.LENGTH_SHORT).show()
            }
            registrationState.value.data != null -> {
                Toast.makeText(context, "Account Created", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.HomeScreen){
                    popUpTo(Routes.SignUpScreen) {inclusive = true}
                }
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
                if (registrationState.value.isLoading) {
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
                        text = "Signup",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 26.sp,
                        modifier = Modifier.padding(bottom = 32.dp),
                        fontFamily = fontFamily
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ){
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(RoundedCornerShape(40.dp))
                                .background(CustomColors.mediumGray)
                                .clickable {
                                    launcher.launch("image/*")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            userImage?.let { uri ->
                                Image(
                                    painter = rememberAsyncImagePainter(uri),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } ?: run {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Upload Image",
                                    tint = CustomColors.darkGray,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }

                        Column {
                            OutlinedTextField(
                                value = userFirstName,
                                colors = outlinedTextFieldStyle,
                                onValueChange = { userFirstName = it },
                                label = { Text("First Name", fontFamily = fontFamily) },
                                shape = RoundedCornerShape(15.dp),
                                modifier = Modifier
                            )

                            OutlinedTextField(
                                value = userLastName,
                                colors = outlinedTextFieldStyle,
                                onValueChange = { userLastName = it },
                                label = { Text("Last Name", fontFamily = fontFamily) },
                                shape = RoundedCornerShape(15.dp),
                                modifier = Modifier
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Email field
                    OutlinedTextField(
                        value = userEmail,
                        colors = outlinedTextFieldStyle,
                        onValueChange = { userEmail = it },
                        label = { Text("Email", fontFamily = fontFamily) },
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Address field
                    OutlinedTextField(
                        value = userAddress,
                        colors = outlinedTextFieldStyle,
                        label = { Text("Address", fontFamily = fontFamily) },
                        onValueChange = { userAddress = it },
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Phone field
                    OutlinedTextField(
                        value = userPhoneNo,
                        colors = outlinedTextFieldStyle,
                        onValueChange = { userPhoneNo = it },
                        label = { Text("Phone", fontFamily = fontFamily) },
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Password field
                    var passwordVisibility by remember { mutableStateOf(false) }
                    OutlinedTextField(
                        value = userPassword,
                        colors = outlinedTextFieldStyle,
                        onValueChange = { userPassword = it },
                        label = { Text("Password", fontFamily = fontFamily) },
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if(userPassword.isNotEmpty()){
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
                            }
                        },
                        visualTransformation = if (passwordVisibility) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = userConfirmPassword,
                        colors = outlinedTextFieldStyle,
                        onValueChange = { userConfirmPassword = it },
                        label = { Text("Confirm Password", fontFamily = fontFamily) },
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "already have account?",
                            color = CustomColors.darkGray,
                            modifier = Modifier.clickable {
                                navController.navigate(Routes.LoginScreen)
                            },
                            textDecoration = TextDecoration.Underline,
                            fontFamily = fontFamily,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(70.dp))

                    Button(
                        onClick = {
                            if (userPassword != userConfirmPassword) {
                                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val userData = UserData(
                                firstName = userFirstName,
                                lastName = userLastName,
                                phoneNo = userPhoneNo,
                                email = userEmail,
                                password = userPassword,
                                address = userAddress
                            )
                            if(userFirstName.isNotEmpty() || userLastName.isNotEmpty() || userEmail.isNotEmpty() || userAddress.isNotEmpty() || userPhoneNo.isNotEmpty()) {
                                if ((userEmail.contains("@") && userEmail.contains(".com"))) {
                                    if (userPassword.length < 6) {
                                        Toast.makeText(
                                            context,
                                            "Password must be at least 6 characters",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        viewModel.registerUserWithEmail(
                                            context = context,
                                            userData = userData,
                                            imageUri = userImage
                                        )
                                        Toast.makeText(
                                            context,
                                            "Account Created",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Incorrect Email Format",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }else{
                                Toast.makeText(context, "Please fill all the fields", Toast.LENGTH_SHORT).show()
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
                        Text("Create Account", fontFamily = fontFamily, color = _root_ide_package_.androidx.compose.ui.graphics.Color.White, fontWeight = FontWeight.Normal)
                    }
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}