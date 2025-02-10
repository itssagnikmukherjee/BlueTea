package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.itssagnikmukherjee.blueteauser.common.ResultState
import com.itssagnikmukherjee.blueteauser.domain.models.UserData
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes

@Composable
fun ProfileScreen(viewModel: ViewModels = hiltViewModel(),
                  navController: NavController,
                  userId: String) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var isEditable by remember { mutableStateOf(false) }
    var changesMade by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    var currentImgUrl by remember { mutableStateOf<String?>(null) }
    var selectedImgUrl by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            selectedImgUrl = uri
            changesMade = true
        }
    )

    LaunchedEffect(Unit) {
        viewModel.getUserDetails(userId)
    }

    val getUserDetailsState = viewModel.getUserDetailsState.collectAsState()
    val updateUserState = viewModel.updateUserState.collectAsState()

    val userData = getUserDetailsState.value.data

    if (getUserDetailsState.value.isLoading) {
        CircularProgressIndicator()
    } else {
        userData?.let {
            firstName = it.firstName
            lastName = it.lastName
            email = it.email
            address = it.address
            currentImgUrl = it.userImage
        }
    }

    LaunchedEffect(updateUserState.value) {
        if (!updateUserState.value.isLoading && updateUserState.value.error == null) {
            viewModel.getUserDetails(userId)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(Color.Gray, CircleShape)
                    .clip(CircleShape)
                    .clickable {
                        if (isEditable) launcher.launch("image/*")
                    }
            ) {
                AsyncImage(
                    model = selectedImgUrl ?: currentImgUrl,
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize()
                )
            }

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it; changesMade = true },
                label = { Text("First Name") },
                readOnly = !isEditable
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it; changesMade = true },
                label = { Text("Last Name") },
                readOnly = !isEditable
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; changesMade = true },
                label = { Text("Email") },
                readOnly = true,
                enabled = false
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it; changesMade = true },
                label = { Text("Address") },
                readOnly = !isEditable
            )

            Button(onClick = {
                if (isEditable && changesMade) {
                    viewModel.updateUserDetails(
                        userId,
                        UserData(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            address = address,
                        ),
                        selectedImgUrl,
                        context
                    )
                }
                isEditable = !isEditable
            }) {
                Text(if (isEditable) "Save Changes" else "Edit Profile")
            }

            Button(onClick = {}) {
                Text("Change Password")
            }

            if (showLogoutDialog){
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Logout") },
                    text = { Text("Are you sure you want to logout?") },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.logout()
                            navController.popBackStack(Routes.LoginScreen, inclusive = false)
                            navController.navigate(Routes.LoginScreen){
                                popUpTo(Routes.LoginScreen){
                                    inclusive = true
                                }
                            }
                        }){
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        Button(onClick = { showLogoutDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            Button(onClick = {
                showLogoutDialog = true
            }) {
                Text("Logout")
            }
        }
    }
}
