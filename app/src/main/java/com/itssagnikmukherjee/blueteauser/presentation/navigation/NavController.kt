package com.itssagnikmukherjee.blueteauser.presentation.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import com.itssagnikmukherjee.blueteauser.presentation.screens.BuyNowScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.CartScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.HomeScreenUser
import com.itssagnikmukherjee.blueteauser.presentation.screens.LoginScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.OrdersScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.ProductDetailsScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.ProfileScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.SignUpScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.WishListScreen
import com.stripe.android.paymentsheet.PaymentSheet

@Composable
fun AppNavigation(modifier: Modifier = Modifier, firebaseAuth: FirebaseAuth, navController: NavHostController, paymentSheet: PaymentSheet) {
    val startDestination = if (firebaseAuth.currentUser == null) Routes.LoginScreen else Routes.HomeScreen

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }) }
    ) {
        composable<Routes.LoginScreen> { LoginScreen(navController = navController) }
        composable<Routes.SignUpScreen> { SignUpScreen(navController = navController) }
        composable<Routes.HomeScreen> { HomeScreenUser(navController = navController) }

        composable<Routes.WishListScreen>{
            val data = it.toRoute<Routes.WishListScreen>()
            WishListScreen(navController = navController, userId = data.userId)
        }

        composable<Routes.CartScreen>{
            val data = it.toRoute<Routes.CartScreen>()
            CartScreen(navController = navController, userId = data.userId)
        }

        composable<Routes.ProfileScreen> {
            val data = it.toRoute<Routes.ProfileScreen>()
            ProfileScreen(navController = navController, userId = data.userId)
        }

        composable<Routes.ProductDetailsScreen> {
            val data = it.toRoute<Routes.ProductDetailsScreen>()
            ProductDetailsScreen(navController = navController, productId = data.productId, userId = data.userId)
        }

        composable<Routes.BuyNowScreen>{
            val data = it.toRoute<Routes.BuyNowScreen>()
            BuyNowScreen(navController = navController, cartItems = data.products, userId = data.userId, quantity = data.quantity.toString(), paymentSheet = paymentSheet)
        }

        composable<Routes.OrdersScreen>{
            val data = it.toRoute<Routes.OrdersScreen>()
            OrdersScreen(navController = navController, userId = data.userId)
        }

    }
}