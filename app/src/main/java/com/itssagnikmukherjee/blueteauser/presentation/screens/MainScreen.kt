package com.itssagnikmukherjee.blueteauser.presentation.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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

    val currentScreen = currentRoute.substringAfterLast(".")

    val hideBottomNavRoutes = setOf(
        Routes.LoginScreen::class.simpleName,
        Routes.SignUpScreen::class.simpleName
    )

    val showBottomNav = currentScreen !in hideBottomNavRoutes &&
            !currentScreen.contains("ProductDetailsScreen", ignoreCase = true)

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNav,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
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
