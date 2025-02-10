package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.graphics.Color
import android.graphics.Outline
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Colors
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.itssagnikmukherjee.blueteauser.domain.models.UserData
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes


@Composable
fun SignUpScreen(viewModel: ViewModels = hiltViewModel(), navController: NavController) {
    val registrationState = viewModel.registerUserState.collectAsState()
    val context = LocalContext.current

    when {
        registrationState.value.isLoading -> {
            CircularProgressIndicator()
        }
        registrationState.value.data != null -> {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
        registrationState.value.error.isNotBlank() -> {
            Toast.makeText(context, registrationState.value.error, Toast.LENGTH_SHORT).show()
        }
    }

    var userFirstName by remember { mutableStateOf("") }
    var userLastName by remember { mutableStateOf("") }
    var userPhoneNo by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }
    var userAddress by remember { mutableStateOf("") }
    var userImage by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        userImage = uri
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape).background(color = androidx.compose.ui.graphics.Color.Gray)
                .clickable {
                    launcher.launch("image/*")
                }
        ) {
            userImage?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = "User Image"
                )
            }
        }
        Row {
            OutlinedTextField(value = userFirstName, onValueChange = { userFirstName = it }, placeholder = { Text("First Name") })
            OutlinedTextField(value = userLastName, onValueChange = { userLastName = it }, placeholder = { Text("Last Name") })
        }
        OutlinedTextField(value = userPhoneNo, onValueChange = { userPhoneNo = it }, placeholder = { Text("Phone No") })
        OutlinedTextField(value = userEmail, onValueChange = { userEmail = it }, placeholder = { Text("Email") })
        OutlinedTextField(value = userPassword, onValueChange = { userPassword = it }, placeholder = { Text("Password") })
        OutlinedTextField(value = userAddress, onValueChange = { userAddress = it }, placeholder = { Text("Address") })

        Button(onClick = {
            val userData = UserData(
                firstName = userFirstName,
                lastName = userLastName,
                phoneNo = userPhoneNo,
                email = userEmail,
                password = userPassword,
                address = userAddress,
                userImage = userImage.toString()
            )
            viewModel.registerUserWithEmail(context = context, userData = userData, imageUri = userImage)
            navController.navigate(Routes.HomeScreen)
            Toast.makeText(context,"Registration Successful",Toast.LENGTH_SHORT).show()
        }) {
            Text("Register")
        }
    }
}