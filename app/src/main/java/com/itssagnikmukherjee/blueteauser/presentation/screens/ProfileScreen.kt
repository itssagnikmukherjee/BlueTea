package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.itssagnikmukherjee.blueteauser.R
import com.itssagnikmukherjee.blueteauser.common.ResultState
import com.itssagnikmukherjee.blueteauser.domain.models.UserData
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.CustomActionButton
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.CustomButton1
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.CustomButtonFilled
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.CustomIconButton
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.HeadingTextWithBadge
import com.itssagnikmukherjee.blueteauser.presentation.theme.CustomColors
import com.itssagnikmukherjee.blueteauser.presentation.theme.fontFamily

@Composable
fun ProfileScreen(viewModel: ViewModels = hiltViewModel(),
                  navController: NavController,
                  userId: String) {

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") } // Added phone field to match UI
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
        ShimmerScreen()
    } else {
        userData?.let {
            firstName = it.firstName
            lastName = it.lastName
            email = it.email
            address = it.address
            phone = it.phoneNo
            currentImgUrl = it.userImage
        }
    }

    LaunchedEffect(updateUserState.value) {
        if (!updateUserState.value.isLoading && updateUserState.value.error == null) {
            viewModel.getUserDetails(userId)
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

        // Define consistent styling for all text fields
        val outlinedTextFieldStyle = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = CustomColors.darkGray,
            unfocusedBorderColor = CustomColors.darkGray,
            focusedLabelColor = CustomColors.darkGray,
            unfocusedLabelColor = CustomColors.darkGray,
            cursorColor = CustomColors.darkGray,
            focusedTextColor = CustomColors.darkGray,
            unfocusedTextColor = CustomColors.primaryBlack,
        )

        Column(
            modifier = Modifier
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomIconButton(
                    onClick = { navController.popBackStack() },
                    icon = R.drawable.back,
                    contentDescription = "back"
                )
            }

            Box(
                modifier = Modifier
                    .size(250.dp)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = selectedImgUrl ?: currentImgUrl,
                    contentDescription = "",
                    modifier = Modifier.size(218.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(CustomColors.darkGray)
                        .clickable {
                            if (isEditable) launcher.launch("image/*")
                        }
                )

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .clickable {
                            if (isEditable && changesMade) {
                                saveUserData(viewModel, userId, firstName, lastName, email, address, phone, selectedImgUrl, context)
                            }
                            isEditable = !isEditable
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id =
                            if (isEditable) R.drawable.tick else R.drawable.edit),
                        contentDescription = "edit",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Form fields with consistent styling
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        colors = outlinedTextFieldStyle,
                        onValueChange = { firstName = it; changesMade = true },
                        label = { Text("First Name", fontFamily = fontFamily) },
                        readOnly = !isEditable,
                        shape = RoundedCornerShape(15.dp),
                        textStyle = TextStyle.Default.copy(fontFamily = fontFamily),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = lastName,
                        colors = outlinedTextFieldStyle,
                        onValueChange = { lastName = it; changesMade = true },
                        label = { Text("Last Name", fontFamily = fontFamily) },
                        readOnly = !isEditable,
                        shape = RoundedCornerShape(15.dp),
                        textStyle = TextStyle.Default.copy(fontFamily = fontFamily),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Address field
                OutlinedTextField(
                    value = address,
                    colors = outlinedTextFieldStyle,
                    onValueChange = { address = it; changesMade = true },
                    label = { Text("Address", fontFamily = fontFamily) },
                    readOnly = !isEditable,
                    shape = RoundedCornerShape(15.dp),
                    textStyle = TextStyle.Default.copy(fontFamily = fontFamily),
                    modifier = Modifier.fillMaxWidth()
                )

                // Phone field
                OutlinedTextField(
                    value = phone,
                    colors = outlinedTextFieldStyle,
                    onValueChange = { phone = it; changesMade = true },
                    label = { Text("Phone", fontFamily = fontFamily) },
                    readOnly = !isEditable,
                    shape = RoundedCornerShape(15.dp),
                    textStyle = TextStyle.Default.copy(fontFamily = fontFamily),
                    modifier = Modifier.fillMaxWidth()
                )

                // Email field
                OutlinedTextField(
                    value = email,
                    colors = outlinedTextFieldStyle,
                    onValueChange = { email = it; changesMade = true },
                    label = { Text("Email", fontFamily = fontFamily) },
                    readOnly = true,
                    enabled = false,
                    shape = RoundedCornerShape(15.dp),
                    textStyle = TextStyle.Default.copy(fontFamily = fontFamily),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = { /* Implement change password logic */ },
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        "Change Password",
                        style = TextStyle(
                            textDecoration = TextDecoration.Underline,
                            fontFamily = fontFamily,
                            color = Color.Black
                        )
                    )
                }

                Button(
                    onClick = { showLogoutDialog = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = Color.Black
                    ),
                    border = BorderStroke(2.dp, CustomColors.primaryBlack),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("Logout", fontFamily = fontFamily)
                }
            }
        }
    }


    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", fontFamily = fontFamily, fontWeight = FontWeight.SemiBold, color = CustomColors.primaryBlack) },
            text = { Text("Are you sure you want to logout?", fontFamily = fontFamily, fontWeight = FontWeight.Normal, color = CustomColors.primaryBlack) },
            confirmButton = {
                CustomButton1(onclick = {
                    viewModel.logout()
                    navController.navigate(Routes.LoginScreen) {
                        popUpTo(0) { inclusive = true }
                    }
                }, text = "Confirm")
            },
            dismissButton = {
                CustomButtonFilled(onclick = {
                    showLogoutDialog = false
                }, text = "Go Back")
            },
        )
    }
}


private fun saveUserData(
    viewModel: ViewModels,
    userId: String,
    firstName: String,
    lastName: String,
    email: String,
    address: String,
    phone: String,
    selectedImgUrl: Uri?,
    context: Context
) {
    viewModel.updateUserDetails(
        userId,
        UserData(
            firstName = firstName,
            lastName = lastName,
            email = email,
            address = address,
            phoneNo = phone
        ),
        selectedImgUrl,
        context
    )
}
