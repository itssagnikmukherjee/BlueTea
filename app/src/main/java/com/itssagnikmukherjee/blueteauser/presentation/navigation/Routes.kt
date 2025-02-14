package com.itssagnikmukherjee.blueteauser.presentation.navigation

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
    object WishListScreen

    @Serializable
    object CartScreen

    @Serializable
    object CheckoutScreen

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