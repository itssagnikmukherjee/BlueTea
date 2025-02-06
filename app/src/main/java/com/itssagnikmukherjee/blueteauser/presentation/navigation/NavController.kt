package com.itssagnikmukherjee.blueteauser.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.itssagnikmukherjee.blueteauser.presentation.screens.HomeScreenUser
import com.itssagnikmukherjee.blueteauser.presentation.screens.LoginScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.ProductDetailsScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.ProfileScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.SignUpScreen

@Composable
fun AppNavigation(modifier: Modifier = Modifier, firebaseAuth: FirebaseAuth) {

//    val startDestination = if(firebaseAuth.currentUser == null)  Routes.LoginScreen else Routes.HomeScreen

    val startDestination = Routes.SignUpScreen

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ){
        composable<Routes.LoginScreen>{ LoginScreen(navController = navController) }
        composable<Routes.SignUpScreen>{ SignUpScreen(navController = navController) }
        composable<Routes.HomeScreen>{ HomeScreenUser(navController = navController) }
        composable<Routes.ProfileScreen>{ ProfileScreen(navController = navController) }
        composable<Routes.ProductDetailsScreen>{ProductDetailsScreen(navController = navController, productId = "")}
    }
}