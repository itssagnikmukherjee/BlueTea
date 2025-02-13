package com.itssagnikmukherjee.blueteauser.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.itssagnikmukherjee.blueteauser.presentation.navigation.AppNavigation

@Composable
fun MainScreen(modifier: Modifier = Modifier, navController: NavController, userId: String, firebaseAuth: FirebaseAuth) {
   Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavbarComposable(navController = navController, userId = userId)
        }
    ) { innerPadding ->
       Box(modifier.padding(innerPadding)){
            AppNavigation(firebaseAuth = firebaseAuth, navController = navController as NavHostController)
       }
    }
}