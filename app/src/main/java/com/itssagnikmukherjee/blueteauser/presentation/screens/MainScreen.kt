package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import com.itssagnikmukherjee.blueteauser.presentation.navigation.AppNavigation
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes

@Composable
fun MainScreen(navController: NavHostController, firebaseAuth: FirebaseAuth, userId: String) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route ?: ""

    // Extract only the class name from the full package path
    val currentScreen = currentRoute.substringAfterLast(".")

    // Routes where BottomNav should be hidden
    val hideBottomNavRoutes = setOf(
        Routes.LoginScreen::class.simpleName,
        Routes.SignUpScreen::class.simpleName,
        Routes.ProductDetailsScreen::class.simpleName
    )

    val showBottomNav = currentScreen !in hideBottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavbarComposable(navController,userId)
            }
        }
    ) { innerPadding ->
        AppNavigation(
            modifier = Modifier.padding(innerPadding),
            firebaseAuth = firebaseAuth,
            navController = navController
        )
    }
}
