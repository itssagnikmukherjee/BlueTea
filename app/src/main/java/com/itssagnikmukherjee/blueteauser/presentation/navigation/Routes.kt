package com.itssagnikmukherjee.blueteauser.presentation.navigation

import androidx.compose.ui.graphics.drawscope.Stroke
import com.itssagnikmukherjee.blueteauser.domain.models.Product
import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {
    @Serializable
    object LoginScreen

    @Serializable
    object SignUpScreen

    @Serializable
    object HomeScreen

    @Serializable
    data class ProfileScreen(
        val userId: String
    )

    @Serializable
    data class WishListScreen(
        val userId: String
    )

    @Serializable
    data class CartScreen(
        val userId: String
    )

    @Serializable
    data class BuyNowScreen(
        val products: List<String>,
        val totalPrice: Double,
        val userId: String
    )

    @Serializable
    object PaymentScreen

    @Serializable
    object SeeAllProductScreen

    @Serializable
    data class ProductDetailsScreen(
        val productId: String,
        val userId: String
    )

}