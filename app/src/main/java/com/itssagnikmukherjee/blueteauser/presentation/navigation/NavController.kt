package com.itssagnikmukherjee.blueteauser.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import com.itssagnikmukherjee.blueteauser.presentation.ViewModels
import com.itssagnikmukherjee.blueteauser.presentation.screens.BuyNowScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.CartScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.CategoryScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.HomeScreenUser
import com.itssagnikmukherjee.blueteauser.presentation.screens.LoginScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.OrdersScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.ProductDetailsScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.ProfileScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.SignUpScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.TrackOrderScreen
import com.itssagnikmukherjee.blueteauser.presentation.screens.WishListScreen
import com.stripe.android.paymentsheet.PaymentSheet

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    firebaseAuth: FirebaseAuth,
    navController: NavHostController,
    paymentSheet: PaymentSheet,
    viewModel: ViewModels = hiltViewModel()
) {
    val startDestination = if (firebaseAuth.currentUser == null) Routes.LoginScreen else Routes.HomeScreen
    val currentUser = firebaseAuth.currentUser
    val currentUserId = currentUser?.uid ?: ""

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

        composable<Routes.WishListScreen> {
            WishListScreen(navController = navController, userId = currentUserId)
        }

        composable<Routes.CartScreen> {
            CartScreen(navController = navController, userId = currentUserId)
        }

        composable<Routes.ProfileScreen> {
            ProfileScreen(navController = navController, userId = currentUserId)
        }

        composable<Routes.ProductDetailsScreen> {
            val data = it.toRoute<Routes.ProductDetailsScreen>()
            ProductDetailsScreen(navController = navController, productId = data.productId, userId = currentUserId)
        }

        composable<Routes.BuyNowScreen> {
            val data = it.toRoute<Routes.BuyNowScreen>()
            BuyNowScreen(
                navController = navController,
                cartItems = data.products,
                userId = currentUserId,
                quantity = data.quantity.toString(),
                paymentSheet = paymentSheet
            )
        }

        composable<Routes.OrdersScreen> {
            OrdersScreen(navController = navController, userId = currentUserId)
        }

        composable<Routes.TrackOrderScreen> {
            val data = it.toRoute<Routes.TrackOrderScreen>()
            TrackOrderScreen(navController = navController, orderId = data.orderId, userId = currentUserId)
        }

        composable<Routes.CategoryScreen> {
            val data = it.toRoute<Routes.CategoryScreen>()
            CategoryScreen(category = data.category, navController = navController, userId = currentUserId)
        }
    }
}