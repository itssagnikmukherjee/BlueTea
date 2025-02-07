package com.itssagnikmukherjee.blueteauser.presentation.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels

@Composable
fun ProfileScreen(viewModel: ViewModels = hiltViewModel(), navController: NavController) {
    val getUserDetailsState = viewModel.getUserDetailsState.collectAsState()

}