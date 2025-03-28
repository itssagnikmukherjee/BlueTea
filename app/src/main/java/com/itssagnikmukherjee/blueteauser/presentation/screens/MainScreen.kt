package com.itssagnikmukherjee.blueteauser.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import com.itssagnikmukherjee.blueteauser.presentation.navigation.AppNavigation
import com.itssagnikmukherjee.blueteauser.presentation.navigation.Routes
import com.itssagnikmukherjee.blueteauser.presentation.screens.components.NavbarComposable
import com.stripe.android.paymentsheet.PaymentSheet

@Composable
fun MainScreen(navController: NavHostController, firebaseAuth: FirebaseAuth, userId: String, paymentSheet: PaymentSheet) {
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route ?: ""

    val currentScreen = currentRoute.substringAfterLast(".")

    val hideBottomNavRoutes = setOf(
        Routes.LoginScreen::class.simpleName,
        Routes.SignUpScreen::class.simpleName,
    )

    val hideBottomNavStrings = listOf<String>(
        "ProductDetailsScreen",
        "BuyNowScreen",
        "OrdersScreen",
        "TrackOrderScreen"
    )

    val showBottomNav = currentScreen !in hideBottomNavRoutes &&
            hideBottomNavStrings.none { currentRoute.contains(it) }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomNav,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                NavbarComposable(navController, userId)
            }
        }
    ) { innerPadding ->
        AppNavigation(
            modifier = Modifier.padding(innerPadding),
            firebaseAuth = firebaseAuth,
            navController = navController,
            paymentSheet = paymentSheet
        )
    }
}
